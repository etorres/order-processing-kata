package es.eriktorr.katas.orders.domain.services;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.jms.OrderPlacedEventSender;
import io.vavr.control.Try;

public class OrderPlacer {

    private final EventStoreRepository eventStoreRepository;
    private final OrderPlacedEventSender orderPlacedEventSender;

    public OrderPlacer(EventStoreRepository eventStoreRepository, OrderPlacedEventSender orderPlacedEventSender) {
        this.eventStoreRepository = eventStoreRepository;
        this.orderPlacedEventSender = orderPlacedEventSender;
    }

    public OrderPlacedEvent place(Order order) {
        return Try.ofSupplier(() -> eventStoreRepository.orderPlacedEventFrom(order))
                .onSuccess(orderPlacedEventSender::send)
                .get();
    }

}