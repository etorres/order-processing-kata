package es.eriktorr.katas.orders.domain.model;

import es.eriktorr.katas.orders.domain.events.DomainEvent;
import es.eriktorr.katas.orders.domain.events.DomainEventMetadata;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class OrderCreatedEvent implements DomainEvent<OrderId, Order> {

    public static final String ORDER_CREATED_EVENT_HANDLE = "order.created";
    public static final int SCHEMA_VERSION = 1;

    private final
    long eventId;

    private final
    DomainEventMetadata metadata;

    private final
    String handle;

    private final
    OrderId aggregateId;

    private final
    Order value;

    public static OrderCreatedEvent build(long eventId, LocalDateTime timestamp, Order order) {
        return new OrderCreatedEvent(
                eventId,
                new DomainEventMetadata(timestamp, SCHEMA_VERSION),
                ORDER_CREATED_EVENT_HANDLE,
                order.getOrderId(),
                order);
    }

}