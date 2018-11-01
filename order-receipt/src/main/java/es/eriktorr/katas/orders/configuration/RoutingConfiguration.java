package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import es.eriktorr.katas.orders.domain.model.OrderRequest;
import es.eriktorr.katas.orders.domain.model.StoreId;
import es.eriktorr.katas.orders.domain.service.OrderReceiver;
import es.eriktorr.katas.orders.domain.service.OrderReceiver2;
import lombok.val;
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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.net.URI;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class RoutingConfiguration {

    private final Validator validator;

    public RoutingConfiguration(Validator validator) {
        this.validator = validator;
    }

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
                .flatMap(this::validate)
                .flatMap(orderReceiver::save)
                .flatMap(order -> ServerResponse.created(URI.create("/orders/" + order.getOrderId())).build())
                .onErrorResume(ConstraintViolationException.class, this::toBadRequest)
        );
    }

    private Mono<? extends Order> orderFrom(ServerRequest request, OrderRequest orderRequest) {
        return Mono.just(new Order(
                orderIdGenerator().nextOrderId(),
                new StoreId(request.pathVariable("storeId")),
                orderRequest.getOrderReference()
        ));
    }

    private Mono<? extends Order> validate(Order order) {
        val violations = validator.validate(order);
        return violations.isEmpty() ? Mono.just(order) : Mono.error(new ConstraintViolationException(violations));
    }

    private Mono<? extends ServerResponse> toBadRequest(ConstraintViolationException exception) {
        val errors = exception.getConstraintViolations().parallelStream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .syncBody(Problem.valueOf(Status.BAD_REQUEST, errors));
    }

}