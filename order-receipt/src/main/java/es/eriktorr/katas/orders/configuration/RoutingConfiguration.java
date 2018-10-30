package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.service.OrderReceiver;
import es.eriktorr.katas.orders.domain.service.OrderReceiver2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.validation.ValidationException;
import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.created;

@Configuration
class RoutingConfiguration {

    @Bean
    OrderReceiver2 orderReceiver2() {
        return new OrderReceiver2();
    }

    @Bean
    OrderReceiver orderReceiver(JmsTemplate jmsTemplate) {
        return new OrderReceiver(jmsTemplate);
    }

    @Bean
    RouterFunction<ServerResponse> createOrderRoute(OrderReceiver orderReceiver) {
        return route(POST("/orders"),
                request -> request.bodyToMono(Order.class)
                        .flatMap(orderReceiver::save)
                        .flatMap(order -> created(URI.create("/orders/" + order.getUuid())).build())
                        .onErrorResume(ValidationException.class, e -> {
                            return ServerResponse.badRequest()
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .syncBody("{\"error\":\"" + e.getMessage() + "\"}");
                        })
        );
    }

}