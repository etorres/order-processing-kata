package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class Order {

    @NotNull(message = "Order Id is needed") private final
    OrderId orderId;

    @NotNull(message = "Store Id is needed") private final
    StoreId storeId;

    @NotNull(message = "Order reference is needed") private final
    OrderReference orderReference;

    public static final Order INVALID = new Order(
            new OrderId("-"), new StoreId("-"), new OrderReference("-")
    );

}