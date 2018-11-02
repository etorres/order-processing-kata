package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.model.Order;
import org.springframework.jms.core.JmsTemplate;
import reactor.core.publisher.Mono;

import static es.eriktorr.katas.orders.configuration.JmsConfiguration.ORDER_QUEUE;

public class OrderEventSender {

    private final JmsTemplate jmsTemplate;

    public OrderEventSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Mono<Order> sendCreatedEvent(Order order) {
        return Mono.defer(() -> {
            jmsTemplate.convertAndSend(ORDER_QUEUE, order); // TODO : subscribe
            return Mono.just(order);
        });
    }

}