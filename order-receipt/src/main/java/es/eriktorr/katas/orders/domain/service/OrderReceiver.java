package es.eriktorr.katas.orders.domain.service;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.jms.OrderEventSender;
import reactor.core.publisher.Mono;

public class OrderReceiver {

    private final EventStoreRepository eventStoreRepository;
    private final OrderEventSender orderEventSender;

    public OrderReceiver(EventStoreRepository eventStoreRepository, OrderEventSender orderEventSender) {
        this.eventStoreRepository = eventStoreRepository;
        this.orderEventSender = orderEventSender;
    }

    public Mono<Order> save(Order order) {
        return eventStoreRepository.save(order)
                .flatMap(orderEventSender::sendCreatedEvent);
    }

}