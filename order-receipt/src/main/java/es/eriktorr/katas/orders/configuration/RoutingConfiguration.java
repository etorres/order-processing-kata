package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import es.eriktorr.katas.orders.domain.model.OrderRequest;
import es.eriktorr.katas.orders.domain.model.StoreId;
import es.eriktorr.katas.orders.domain.service.OrderReceiver;
import es.eriktorr.katas.orders.domain.service.OrderReceiver2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import reactor.core.publisher.Mono;

import javax.validation.ValidationException;
import javax.validation.Validator;
import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class RoutingConfiguration {

    @Bean
    OrderReceiver2 orderReceiver2() {
        return new OrderReceiver2();
    }

    @Bean
    OrderIdGenerator orderIdGenerator() {
        return new OrderIdGenerator();
    }

    @Bean
    OrderReceiver orderReceiver(JmsTemplate jmsTemplate) {
        return new OrderReceiver(jmsTemplate);
    }

    @Bean
    RouterFunction<ServerResponse> createOrderRoute(OrderReceiver orderReceiver, Validator validator) {
        return route(POST("/stores/{storeId}/orders"), request -> request.bodyToMono(OrderRequest.class)
                .flatMap(orderRequest -> orderFrom(request, orderRequest))
                .flatMap(order -> {

                    System.err.println("\n\n >> HERE: " + validator.validate(order) + "\n");

                    return validator.validate(order).isEmpty() ? Mono.just(order) : Mono.error(new ValidationException("KK"));
                })
                .flatMap(orderReceiver::save)
                .flatMap(order -> ServerResponse.created(URI.create("/orders/" + order.getOrderId())).build())
                .onErrorResume(ValidationException.class, this::toBadRequest)
        );



//                request -> request.body  .bodyToMono(Order.class)
//                        .flatMap(orderReceiver::save)
//                        .flatMap(order -> ServerResponse.created(URI.create("/orders/" + order.getOrderId())).build())
//                        .onErrorResume(ValidationException.class, this::toBadRequest)
//        );
    }

    private Mono<? extends Order> orderFrom(ServerRequest request, OrderRequest orderRequest) {
        return Mono.just(new Order(
                orderIdGenerator().nextOrderId(),
                new StoreId(request.pathVariable("storeId")),
                orderRequest.getOrderReference()
        ));
    }

    private Mono<? extends ServerResponse> toBadRequest(ValidationException e) {
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .syncBody(Problem.valueOf(Status.BAD_REQUEST, e.getMessage()));
    }

}