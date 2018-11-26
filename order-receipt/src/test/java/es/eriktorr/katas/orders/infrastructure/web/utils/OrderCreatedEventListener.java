package es.eriktorr.katas.orders.infrastructure.web.utils;

import es.eriktorr.katas.orders.domain.common.DomainEventMetadata;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
import es.eriktorr.katas.orders.domain.model.OrderId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Slf4j
public class OrderCreatedEventListener {

    private static final int MAX_EVENTS_COUNT = 10;

    private static final List<OrderCreatedEvent> EVENTS = Collections.synchronizedList(new ArrayList<>(10));

    private final AtomicInteger count = new AtomicInteger(0);

    @JmsListener(destination = "${order.created.event.queue.name}", containerFactory = "jmsListenerContainerFactory")
    public void process(OrderCreatedEvent orderCreatedEvent) {
        if (count.getAndIncrement() < MAX_EVENTS_COUNT) {
            EVENTS.add(orderCreatedEvent);
        }
        log.info("Event received: " + orderCreatedEvent);
    }

    public boolean eventReceived(OrderCreatedEvent orderCreatedEvent) {
        return EVENTS.stream().anyMatch(
                hasMetadata(orderCreatedEvent.getMetadata())
                        .and(hasHandle(orderCreatedEvent.getHandle()))
                        .and(hasAggregateId(orderCreatedEvent.getAggregateId()))
                        .and(hasValue(orderCreatedEvent.getValue()))
        );
    }

    private static Predicate<OrderCreatedEvent> hasMetadata(DomainEventMetadata metadata) {
        return event -> event.getMetadata().equals(metadata);
    }

    private static Predicate<OrderCreatedEvent> hasHandle(String handle) {
        return event -> event.getHandle().equals(handle);
    }

    private static Predicate<OrderCreatedEvent> hasAggregateId(OrderId aggregateId) {
        return event -> event.getAggregateId().equals(aggregateId);
    }

    private static Predicate<OrderCreatedEvent> hasValue(Order value) {
        return event -> event.getValue().equals(value);
    }

}