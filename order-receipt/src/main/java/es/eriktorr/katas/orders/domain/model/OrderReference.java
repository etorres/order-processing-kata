package es.eriktorr.katas.orders.domain.model;

import es.eriktorr.katas.orders.domain.common.SingleValue;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class OrderReference implements SingleValue {

    @NotBlank(message = "Order reference cannot be blank") private final
    String value;

    @Override
    public String toString() {
        return value;
    }

}