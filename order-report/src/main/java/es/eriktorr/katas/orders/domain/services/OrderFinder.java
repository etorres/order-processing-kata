package es.eriktorr.katas.orders.domain.services;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.StoreId;
import es.eriktorr.katas.orders.infrastructure.database.OrdersRepository;
import reactor.core.publisher.Mono;

public class OrderFinder {

    private final OrdersRepository ordersRepository;

    public OrderFinder(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public Mono<Order> findOrderBy(StoreId storeId, OrderId orderId) {
        return ordersRepository.findOrderBy(storeId, orderId);
    }

}