package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class StoreId {

    @NotBlank(message = "Store Id cannot be empty") private final
    String value;

    @Override
    public String toString() {
        return value;
    }

}