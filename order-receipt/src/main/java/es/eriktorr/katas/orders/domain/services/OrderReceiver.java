package es.eriktorr.katas.orders.domain.services;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.jms.OrderCreatedEventSender;
import reactor.core.publisher.Mono;

public class OrderReceiver {

    private final EventStoreRepository eventStoreRepository;
    private final OrderCreatedEventSender orderCreatedEventSender;

    public OrderReceiver(EventStoreRepository eventStoreRepository, OrderCreatedEventSender orderCreatedEventSender) {
        this.eventStoreRepository = eventStoreRepository;
        this.orderCreatedEventSender = orderCreatedEventSender;
    }

    public Mono<OrderCreatedEvent> save(Mono<Order> order) {
        return order.compose(it -> eventStoreRepository.save(it)
                .doOnNext(orderCreatedEventSender::send));
    }

}