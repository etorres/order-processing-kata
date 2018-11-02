package es.eriktorr.katas.orders.infrastructure.database;

import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.common.DomainEvent;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.infrastructure.json.OrderJsonMapper;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class EventStoreRepository {

    private final JdbcTemplate jdbcTemplate;
    private final OrderJsonMapper jsonMapper;
    private final Clock clock;

    public EventStoreRepository(JdbcTemplate jdbcTemplate, OrderJsonMapper orderJsonMapper, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.jsonMapper = orderJsonMapper;
        this.clock = clock;
    }

    public Mono<Order> save(Order order) {
        return Mono.defer(() -> Mono.just(createdOrderEventFrom(order)))
                .subscribeOn(Schedulers.elastic())
                .map(DomainEvent::getValue);
    }

    private OrderCreatedEvent createdOrderEventFrom(Order order) {
        val keyHolder = new GeneratedKeyHolder();
        val currentTimestamp = clock.currentTimestamp();
        jdbcTemplate.update(connection -> {
            val preparedStatement = connection.prepareStatement(
                    "INSERT INTO event_store (created_at, handle, aggregate_id, payload) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.setTimestamp(1, currentTimestamp);
            preparedStatement.setString(2, OrderCreatedEvent.ORDER_CREATED_EVENT_HANDLE);
            preparedStatement.setString(3, order.getOrderId().getValue());
            preparedStatement.setClob(4, new SerialClob(jsonMapper.toJson(order).toCharArray()));
            return preparedStatement;
        }, keyHolder);
        val eventId = eventIdOrError(generatedKeys(keyHolder));
        return new OrderCreatedEvent(eventId, currentTimestamp.toLocalDateTime(), order);
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