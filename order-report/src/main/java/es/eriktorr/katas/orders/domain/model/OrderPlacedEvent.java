package es.eriktorr.katas.orders.domain.model;

import es.eriktorr.katas.orders.domain.events.DomainEvent;
import es.eriktorr.katas.orders.domain.events.DomainEventMetadata;
import lombok.Value;

@Value
public class OrderPlacedEvent implements DomainEvent<OrderId, Order> {

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

}