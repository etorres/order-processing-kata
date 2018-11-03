package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

    @NotNull(message = "Creation date & time is needed") private final
    LocalDateTime createdAt;

}