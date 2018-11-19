package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.domain.services.OrderFinder;
import es.eriktorr.katas.orders.infrastructure.web.OrderHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class RoutingConfiguration {

    @Bean
    OrderHandler orderHandler(OrderFinder orderFinder) {
        return new OrderHandler(orderFinder);
    }

    @Bean
    RouterFunction<ServerResponse> createOrderRoute(OrderHandler orderHandler) {
        return route(GET("/stores/{storeId}/orders/{orderId}").and(accept(APPLICATION_JSON)),
                orderHandler::getOrder
        );
    }

    @Bean
    RouterFunction<ServerResponse> indexRouter(@Value("classpath:static/index.html") final Resource indexHtml) {
        return route(GET("/"), request -> ServerResponse.ok().contentType(TEXT_HTML).syncBody(indexHtml));
    }

}