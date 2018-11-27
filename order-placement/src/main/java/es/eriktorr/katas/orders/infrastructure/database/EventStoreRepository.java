package es.eriktorr.katas.orders.infrastructure.database;

import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static es.eriktorr.katas.orders.domain.model.OrderPlacedEvent.ORDER_PLACED_EVENT_HANDLE;
import static es.eriktorr.katas.orders.infrastructure.common.EventStorePreparedStatementCreator.preparedStatementFor;

public class EventStoreRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public EventStoreRepository(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    public Mono<OrderPlacedEvent> orderPlacedEventFrom(Order order) {
        return Mono.defer(() -> Mono.just(syncOrderPlacedEventFrom(order)))
                .subscribeOn(Schedulers.elastic());
    }

    private OrderPlacedEvent syncOrderPlacedEventFrom(Order order) {
        val keyHolder = new GeneratedKeyHolder();
        val timestamp = clock.currentTimestamp();
        jdbcTemplate.update(preparedStatementCreatorFor(order, timestamp), keyHolder);
        val eventId = eventIdOrError(generatedKeys(keyHolder));
        return OrderPlacedEvent.build(eventId, timestamp.toLocalDateTime(), order);
    }

    private PreparedStatementCreator preparedStatementCreatorFor(Order order, Timestamp timestamp) {
        val aggregateId = order.getOrderId();
        return connection -> preparedStatementFor(timestamp, ORDER_PLACED_EVENT_HANDLE, aggregateId, connection);
    }

    private Map<String, Object> generatedKeys(GeneratedKeyHolder keyHolder) {
        return Optional.ofNullable(keyHolder.getKeys())
                .orElse(Collections.emptyMap());
    }

    private long eventIdOrError(Map<String, Object> keys) {
        return (long) Optional.ofNullable(keys.get("event_id"))
                .orElseThrow(() -> new IllegalStateException("cannot get autogenerated event Id from database"));
    }

}