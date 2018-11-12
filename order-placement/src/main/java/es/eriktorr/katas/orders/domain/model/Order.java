package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Order {

    private final
    OrderId orderId;

    private final
    StoreId storeId;

    private final
    OrderReference orderReference;

    private final
    LocalDateTime createdAt;

}