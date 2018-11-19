package es.eriktorr.katas.orders.infrastructure.database;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.StoreId;
import org.springframework.jdbc.core.JdbcTemplate;
import reactor.core.publisher.Mono;

public class OrdersRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrdersRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mono<Order> findOrderBy(StoreId storeId, OrderId orderRequest) {
        return Mono.error(() -> new IllegalStateException("Operation not implemented"));
    }

}