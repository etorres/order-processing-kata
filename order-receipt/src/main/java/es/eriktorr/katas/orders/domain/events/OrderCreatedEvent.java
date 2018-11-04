package es.eriktorr.katas.orders.domain.events;

import es.eriktorr.katas.orders.domain.common.DomainEvent;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderId;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends DomainEvent<OrderId, Order> {

    public static final String ORDER_CREATED_EVENT_HANDLE = "order.created";

    public OrderCreatedEvent(long eventId, LocalDateTime timestamp, Order order) {
        super(eventId, new Metadata(timestamp), ORDER_CREATED_EVENT_HANDLE, order.getOrderId(), order);
    }

}