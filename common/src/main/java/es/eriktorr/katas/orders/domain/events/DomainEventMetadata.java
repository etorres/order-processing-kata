package es.eriktorr.katas.orders.domain.events;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class DomainEventMetadata {

    private final
    LocalDateTime timestamp;

    private final
    int schemaVersion;

}