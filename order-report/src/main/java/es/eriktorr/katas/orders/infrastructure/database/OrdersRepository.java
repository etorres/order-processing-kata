package es.eriktorr.katas.orders.infrastructure.database;

import es.eriktorr.katas.orders.domain.model.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.sql.Types;

public class OrdersRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrdersRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mono<Order> findOrderBy(StoreId storeId, OrderId orderId) {
        return Mono.justOrEmpty(syncFindOrderBy(storeId, orderId));
    }

    private Order syncFindOrderBy(StoreId storeId, OrderId orderId) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, store, reference, created_at FROM orders WHERE store = ? AND id = ?",
                    new Object[]{ storeId.getValue(), orderId.getValue() },
                    new int[]{ Types.VARCHAR, Types.VARCHAR },
                    (resultSet, rowNum) -> new Order(
                            new OrderId(resultSet.getString("id")),
                            new StoreId(resultSet.getString("store")),
                            new OrderReference(resultSet.getString("reference")),
                            resultSet.getTimestamp("created_at").toLocalDateTime()
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void save(Order order) {
        jdbcTemplate.update("MERGE INTO orders (id, store, reference, created_at) KEY(id, store) VALUES (?, ?, ?, ?)",
                new Object[]{
                        order.getOrderId().getValue(),
                        order.getStoreId().getValue(),
                        order.getOrderReference().getValue(),
                        Timestamp.valueOf(order.getCreatedAt())
                },
                new int[]{ Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP });
    }

}