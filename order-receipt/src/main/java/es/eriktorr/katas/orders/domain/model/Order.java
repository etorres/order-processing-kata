package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Value
public class Order {

    @Valid
    @NotNull(message = "Order Id is needed") private final
    OrderId orderId;

    @Valid
    @NotNull(message = "Store Id is needed") private final
    StoreId storeId;

    @Valid
    @NotNull(message = "Order reference is needed") private final
    OrderReference orderReference;

}