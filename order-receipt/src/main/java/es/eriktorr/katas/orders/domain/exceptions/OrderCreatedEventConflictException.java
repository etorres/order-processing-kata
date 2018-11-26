package es.eriktorr.katas.orders.domain.exceptions;

public class OrderCreatedEventConflictException extends RuntimeException {

    public OrderCreatedEventConflictException(String message) {
        super(message);
    }

}