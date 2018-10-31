package es.eriktorr.katas.orders.domain.model;

import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@ToString(includeFieldNames = false)
public class OrderReference {

    @NotBlank(message = "Order reference cannot be empty") private final
    String value;

}