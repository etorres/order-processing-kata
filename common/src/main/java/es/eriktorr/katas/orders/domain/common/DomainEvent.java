package es.eriktorr.katas.orders.domain.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DomainEvent<AGGREGATE_ID extends SingleValue, VALUE> {

    @Getter private final
    long eventId;

    @Getter private final
    LocalDateTime createdAt;

    @Getter private final
    String Handle;

    @Getter private final
    AGGREGATE_ID aggregateId;

    @Getter private final
    VALUE value;

}