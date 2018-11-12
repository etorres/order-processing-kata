package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;

public class OrderPlacedEventSender {

    private final JmsTemplate jmsTemplate;
    private final Queue orderPlacedEventQueue;

    public OrderPlacedEventSender(JmsTemplate jmsTemplate, Queue orderPlacedEventQueue) {
        this.jmsTemplate = jmsTemplate;
        this.orderPlacedEventQueue = orderPlacedEventQueue;
    }

    void send(OrderPlacedEvent orderPlacedEvent) {
        jmsTemplate.convertAndSend(orderPlacedEventQueue, orderPlacedEvent);
    }

}