package es.eriktorr.katas.orders.domain.model;

import es.eriktorr.katas.orders.domain.common.SingleValue;

public interface WithIdentifiableOrder {

    <T extends SingleValue> T getOrderId();

}