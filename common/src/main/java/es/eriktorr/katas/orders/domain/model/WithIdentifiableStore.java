package es.eriktorr.katas.orders.domain.model;

import es.eriktorr.katas.orders.domain.common.SingleValue;

public interface WithIdentifiableStore {

    <T extends SingleValue> T getStoreId();

}