package es.eriktorr.katas.orders.domain.model;

import es.eriktorr.katas.orders.domain.common.SingleValue;

public interface WithReferableOrder {

    <T extends SingleValue> T getOrderReference();

}