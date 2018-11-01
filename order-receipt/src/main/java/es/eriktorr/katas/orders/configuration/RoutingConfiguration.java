package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import es.eriktorr.katas.orders.domain.service.OrderReceiver;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.web.OrderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.validation.Validator;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class RoutingConfiguration {

    @Bean
    OrderIdGenerator orderIdGenerator() {
        return new OrderIdGenerator();
    }

    @Bean
    OrderReceiver orderReceiver(EventStoreRepository eventStoreRepository, JmsTemplate jmsTemplate) {
        return new OrderReceiver(eventStoreRepository, jmsTemplate);
    }

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

}