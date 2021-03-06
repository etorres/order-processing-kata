package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Order implements WithOrderInformation {

    private final
    OrderId orderId;

    private final
    StoreId storeId;

    private final
    OrderReference orderReference;

    private final
    LocalDateTime createdAt;

    public static final Order INVALID = new Order(
            new OrderId("-"), new StoreId("-"), new OrderReference("-"), null
    );

}