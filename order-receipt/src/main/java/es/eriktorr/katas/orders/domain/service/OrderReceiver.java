package es.eriktorr.katas.orders.domain.service;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
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

    public Mono<OrderCreatedEvent> save(Mono<Order> order) {
        return order.compose(it -> eventStoreRepository.save(it)
                .doOnNext(orderEventSender::send));
    }

}