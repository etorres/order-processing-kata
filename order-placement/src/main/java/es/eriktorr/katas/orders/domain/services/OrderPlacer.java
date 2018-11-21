package es.eriktorr.katas.orders.domain.services;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.jms.OrderPlacedEventSender;
import reactor.core.publisher.Mono;

public class OrderPlacer {

    private final EventStoreRepository eventStoreRepository;
    private final OrderPlacedEventSender orderPlacedEventSender;

    public OrderPlacer(EventStoreRepository eventStoreRepository, OrderPlacedEventSender orderPlacedEventSender) {
        this.eventStoreRepository = eventStoreRepository;
        this.orderPlacedEventSender = orderPlacedEventSender;
    }

    public Mono<OrderPlacedEvent> place(Order order) {
        return eventStoreRepository.orderPlacedEventFrom(order)
                .doOnNext(orderPlacedEventSender::send);
    }

}