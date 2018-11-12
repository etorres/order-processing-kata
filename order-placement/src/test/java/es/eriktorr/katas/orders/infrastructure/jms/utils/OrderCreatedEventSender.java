package es.eriktorr.katas.orders.infrastructure.jms.utils;

import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;

public class OrderCreatedEventSender {

    private final JmsTemplate jmsTemplate;
    private final Queue orderCreatedEventQueue;

    public OrderCreatedEventSender(JmsTemplate jmsTemplate, Queue orderCreatedEventQueue) {
        this.jmsTemplate = jmsTemplate;
        this.orderCreatedEventQueue = orderCreatedEventQueue;
    }

    public void send(OrderCreatedEvent orderCreatedEvent) {
        jmsTemplate.convertAndSend(orderCreatedEventQueue, orderCreatedEvent);
    }

}