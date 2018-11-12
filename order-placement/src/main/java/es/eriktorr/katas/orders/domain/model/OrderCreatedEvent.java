package es.eriktorr.katas.orders.domain.model;

import es.eriktorr.katas.orders.domain.common.DomainEvent;
import es.eriktorr.katas.orders.domain.common.DomainEventMetadata;
import lombok.Value;

@Value
public class OrderCreatedEvent implements DomainEvent<OrderId, Order> {

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