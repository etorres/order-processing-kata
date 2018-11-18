package es.eriktorr.katas.orders.domain.common;

public interface WithIdentifiableOrder {

    <T extends SingleValue> T getOrderId();

}