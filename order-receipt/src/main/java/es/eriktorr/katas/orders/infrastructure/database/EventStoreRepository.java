package es.eriktorr.katas.orders.infrastructure.database;

import es.eriktorr.katas.orders.domain.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import reactor.core.publisher.Mono;

public class EventStoreRepository {

    private final JdbcTemplate jdbcTemplate;

    public EventStoreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mono<Void> create(Order order) {


        return null;
    }

}