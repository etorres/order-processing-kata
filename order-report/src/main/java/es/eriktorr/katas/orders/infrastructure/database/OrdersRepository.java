package es.eriktorr.katas.orders.infrastructure.database;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.OrderReference;
import es.eriktorr.katas.orders.domain.model.StoreId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import reactor.core.publisher.Mono;

import java.sql.Types;

public class OrdersRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrdersRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mono<Order> findOrderBy(StoreId storeId, OrderId orderId) {
        return Mono.fromSupplier(() -> syncFindOrderBy(storeId, orderId));
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
            return Order.INVALID;
        }
    }

}