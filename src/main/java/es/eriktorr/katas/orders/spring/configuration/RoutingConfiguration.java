package es.eriktorr.katas.orders.spring.configuration;

import es.eriktorr.katas.orders.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.created;

@Configuration
public class RoutingConfiguration {

    @Bean
    public RouterFunction<ServerResponse> createOrderRoute() {
        return route(POST("/orders"), request -> request
                .body(toMono(Order.class))
                .then(created(URI.create("/orders/4472a477-931e-48b7-8bfb-95daa1ad0216")).build()));
    }

}