package es.eriktorr.katas.orders.domain.model;

import java.util.UUID;

public class OrderIdGenerator {

    public OrderId nextOrderId() {
        return new OrderId(UUID.randomUUID().toString());
    }

}