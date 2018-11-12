package es.eriktorr.katas.orders.infrastructure.jms.utils;

import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;

public class OrderEventSender {

    private final JmsTemplate jmsTemplate;
    private final Queue ordersQueue;

    public OrderEventSender(JmsTemplate jmsTemplate, Queue ordersQueue) {
        this.jmsTemplate = jmsTemplate;
        this.ordersQueue = ordersQueue;
    }

    public void send(OrderCreatedEvent orderCreatedEvent) {
        jmsTemplate.convertAndSend(ordersQueue, orderCreatedEvent);
    }

}