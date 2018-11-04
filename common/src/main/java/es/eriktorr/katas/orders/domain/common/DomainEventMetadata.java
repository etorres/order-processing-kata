package es.eriktorr.katas.orders.domain.common;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class DomainEventMetadata {

    private final
    LocalDateTime timestamp;

    private final
    int schemaVersion;

}