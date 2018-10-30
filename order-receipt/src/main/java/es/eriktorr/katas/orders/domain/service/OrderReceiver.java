package es.eriktorr.katas.orders.domain.service;

import es.eriktorr.katas.orders.domain.model.Order;
import org.springframework.jms.core.JmsTemplate;
import reactor.core.publisher.Mono;

import static es.eriktorr.katas.orders.configuration.JmsConfiguration.ORDER_QUEUE;

public class OrderReceiver {

    private final JmsTemplate jmsTemplate;

    public OrderReceiver(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Mono<Order> save(Order order) {
        return Mono.defer(() -> {
            jmsTemplate.convertAndSend(ORDER_QUEUE, order);
            return Mono.just(order);
        });
    }

}