package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class OrderRequest {

    @NotNull(message = "Order reference is needed") private final
    OrderReference orderReference;

}