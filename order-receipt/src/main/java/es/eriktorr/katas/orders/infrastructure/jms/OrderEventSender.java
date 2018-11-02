package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.model.Order;
import org.springframework.jms.core.JmsTemplate;

import static es.eriktorr.katas.orders.configuration.JmsConfiguration.ORDER_QUEUE;

public class OrderEventSender {

    private final JmsTemplate jmsTemplate;

    public OrderEventSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendCreatedEvent(Order order) {
        jmsTemplate.convertAndSend(ORDER_QUEUE, order);
    }

}