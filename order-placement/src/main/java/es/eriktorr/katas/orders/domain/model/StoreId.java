package es.eriktorr.katas.orders.domain.model;

import es.eriktorr.katas.orders.domain.common.SingleValue;
import lombok.Value;

@Value
public class StoreId implements SingleValue {

    private final
    String value;

    @Override
    public String toString() {
        return value;
    }

}