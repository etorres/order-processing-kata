package es.eriktorr.katas.orders.infrastructure.database;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.infrastructure.json.OrderJsonMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import reactor.core.publisher.Mono;

import java.sql.Types;

public class EventStoreRepository {

    private final JdbcTemplate jdbcTemplate;
    private final OrderJsonMapper jsonMapper;

    public EventStoreRepository(JdbcTemplate jdbcTemplate, OrderJsonMapper orderJsonMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.jsonMapper = orderJsonMapper;
    }

    public Mono<Order> save(Order order) {
        return Mono.defer(() -> {
            jdbcTemplate.update("INSERT INTO event_store (timestamp, handle, aggregate_id, payload) VALUES (CURRENT_TIMESTAMP, ?, ?, ?)",
                    new Object[]{ "order.created", "order." + order.getOrderId().getValue(), jsonMapper.toJson(order) },
                    new int[]{ Types.VARCHAR, Types.VARCHAR, Types.CLOB });
            return Mono.just(order);
        });
    }

}