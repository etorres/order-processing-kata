package es.eriktorr.katas.orders.domain.model;

import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@ToString(includeFieldNames = false)
public class StoreId {

    @NotBlank(message = "Store Id cannot be empty") private final
    String value;

}