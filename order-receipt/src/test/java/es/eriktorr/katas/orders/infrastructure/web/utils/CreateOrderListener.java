package es.eriktorr.katas.orders.infrastructure.web.utils;

import es.eriktorr.katas.orders.domain.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

import java.util.concurrent.atomic.AtomicBoolean;

import static es.eriktorr.katas.orders.configuration.JmsConfiguration.ORDER_QUEUE;

@Slf4j
public class CreateOrderListener {

    private final AtomicBoolean messageReceived = new AtomicBoolean(false);

    @JmsListener(destination = ORDER_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void process(Order order) {
        log.info("Message received: " + order);
        messageReceived.set(true);
    }

    public boolean isMessageReceived() {
        return messageReceived.get();
    }

}