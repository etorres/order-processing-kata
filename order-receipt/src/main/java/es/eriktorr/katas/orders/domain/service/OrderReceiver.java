package es.eriktorr.katas.orders.domain.service;

import es.eriktorr.katas.orders.domain.model.Order;
import org.springframework.jms.annotation.JmsListener;

import static es.eriktorr.katas.orders.configuration.JmsConfiguration.ORDER_QUEUE;

public class OrderReceiver {

    @JmsListener(destination = ORDER_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void process(Order order) {
        // TODO
        System.err.println("Received <" + order + ">");
        // TODO
    }

}