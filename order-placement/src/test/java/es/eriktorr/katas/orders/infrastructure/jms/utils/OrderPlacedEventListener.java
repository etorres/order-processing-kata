package es.eriktorr.katas.orders.infrastructure.jms.utils;

import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class OrderPlacedEventListener {

    private static final int MAX_EVENTS_COUNT = 10;

    private static final List<OrderPlacedEvent> EVENTS = Collections.synchronizedList(new ArrayList<>(10));

    private final AtomicInteger count = new AtomicInteger(0);

    @JmsListener(destination = "${order.placed.event.queue.name}", containerFactory = "jmsListenerContainerFactory")
    public void process(OrderPlacedEvent orderPlacedEvent) {
        if (count.getAndIncrement() < MAX_EVENTS_COUNT) {
            EVENTS.add(orderPlacedEvent);
        }
        log.info("Event received: " + orderPlacedEvent);
    }

    public boolean eventReceived(OrderPlacedEvent orderPlacedEvent) {
        return EVENTS.contains(orderPlacedEvent);
    }

}