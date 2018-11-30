package es.eriktorr.katas.orders.infrastructure.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.eriktorr.katas.orders.domain.common.SingleValue;
import es.eriktorr.katas.orders.domain.model.WithOrderInformation;
import es.eriktorr.katas.orders.infrastructure.json.JsonMapper;
import lombok.val;

import java.sql.*;

import static es.eriktorr.katas.orders.domain.common.StringUtils.trimJsonToEmpty;

public final class EventStorePreparedStatementCreator {

    private final ObjectMapper objectMapper;

    public EventStorePreparedStatementCreator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    <T extends WithOrderInformation> PreparedStatement preparedStatementFor(
            Timestamp timestamp, String handle, T value, Connection connection
    ) throws SQLException {
        val preparedStatement = connection.prepareStatement(
                "INSERT INTO event_store (timestamp, handle, aggregate_id, payload) " +
                        "SELECT ? AS timestamp, ? AS handle, ? AS aggregate_id, ?::JSONB AS payload " +
                        "WHERE NOT EXISTS " +
                        "(SELECT handle, aggregate_id FROM event_store WHERE payload->>'store' = ? AND payload->>'reference' = ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, value.getOrderId().getValue());
        preparedStatement.setObject(4, payloadFrom(value, objectMapper));
        preparedStatement.setString(5, value.getStoreId().getValue());
        preparedStatement.setString(6, value.getOrderReference().getValue());
        return preparedStatement;
    }

    PreparedStatement preparedStatementFor(
            Timestamp timestamp, String handle, SingleValue aggregateId, Connection connection
    ) throws SQLException {
        val preparedStatement = connection.prepareStatement("INSERT INTO event_store (timestamp, handle, aggregate_id, payload) VALUES (?, ?, ?, ?::JSONB)",
                Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, aggregateId.getValue());
        preparedStatement.setObject(4, trimJsonToEmpty(null));
        return preparedStatement;
    }

    private static <T> String payloadFrom(T value, ObjectMapper objectMapper) {
        val payload = new JsonMapper<>(objectMapper).toJson(value);
        return trimJsonToEmpty(payload);
    }

}