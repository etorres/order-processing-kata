package es.eriktorr.katas.orders.infrastructure.web.utils;

import es.eriktorr.katas.orders.domain.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static es.eriktorr.katas.orders.configuration.JmsConfiguration.ORDER_QUEUE;

@Slf4j
public class CreateOrderEventListener {

    private static final int MAX_EVENTS_COUNT = 10;

    private final List<Order> orders = Collections.synchronizedList(new ArrayList<>(10));

    private final AtomicInteger count = new AtomicInteger(0);

    @JmsListener(destination = ORDER_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void process(Order order) {
        if (count.getAndIncrement() < MAX_EVENTS_COUNT) {
            orders.add(order);
        }
        log.info("Event received: " + order);
    }

    public boolean eventReceived(Order order) {
        return orders.contains(order);
    }

}