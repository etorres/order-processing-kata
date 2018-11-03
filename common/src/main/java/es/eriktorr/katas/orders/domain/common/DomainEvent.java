package es.eriktorr.katas.orders.domain.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DomainEvent<A extends SingleValue, V> {

    @Getter private final
    long eventId;

    @Getter private final
    LocalDateTime createdAt;

    @Getter private final
    String handle;

    @Getter private final
    A aggregateId;

    @Getter private final
    V value;

}