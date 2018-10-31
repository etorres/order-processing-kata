package es.eriktorr.katas.orders.domain.model;

import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@ToString(includeFieldNames = false)
public class OrderId {

    @NotBlank(message = "Order Id cannot be empty") private final
    String value;

}