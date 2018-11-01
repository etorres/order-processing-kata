package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class OrderId {

    @NotBlank(message = "Order Id cannot be empty") private final
    String value;

    @Override
    public String toString() {
        return value;
    }

}