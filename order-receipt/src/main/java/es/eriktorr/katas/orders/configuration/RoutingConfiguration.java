package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import es.eriktorr.katas.orders.domain.services.OrderReceiver;
import es.eriktorr.katas.orders.infrastructure.web.OrderHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.validation.Validator;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class RoutingConfiguration {

    @Bean
    OrderHandler orderHandler(Validator validator, OrderIdGenerator orderIdGenerator, OrderReceiver orderReceiver) {
        return new OrderHandler(validator, orderIdGenerator, orderReceiver);
    }

    @Bean
    RouterFunction<ServerResponse> createOrderRoute(OrderHandler orderHandler) {
        return route(POST("/stores/{storeId}/orders").and(accept(APPLICATION_JSON)),
                orderHandler::createOrder
        );
    }

    @Bean
    RouterFunction<ServerResponse> indexRouter(@Value("classpath:static/index.html") final Resource indexHtml) {
        return route(GET("/"), request -> ServerResponse.ok().contentType(TEXT_HTML).syncBody(indexHtml));
    }

}