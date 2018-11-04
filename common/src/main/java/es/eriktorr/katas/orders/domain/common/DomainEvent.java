package es.eriktorr.katas.orders.domain.common;

public interface DomainEvent<A extends SingleValue, V> {

    long getEventId();

    DomainEventMetadata getMetadata();

    String getHandle();

    A getAggregateId();

    V getValue();

}