package es.eriktorr.katas.orders.domain.exceptions;

public class OrderPlacedEventConflictException extends RuntimeException {

    public OrderPlacedEventConflictException(String message) {
        super(message);
    }

}