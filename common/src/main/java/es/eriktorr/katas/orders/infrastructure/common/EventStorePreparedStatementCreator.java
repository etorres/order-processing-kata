package es.eriktorr.katas.orders.infrastructure.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.eriktorr.katas.orders.domain.common.SingleValue;
import lombok.val;

import java.sql.*;

import static es.eriktorr.katas.orders.domain.common.StringUtils.trimJsonToEmpty;

public final class EventStorePreparedStatementCreator {

    private EventStorePreparedStatementCreator() {}

    public static <T> PreparedStatement preparedStatementFor(
            Timestamp timestamp, String handle, SingleValue aggregateId,
            SingleValue store, SingleValue reference,
            T value, Class<T> valueType,
            ObjectMapper objectMapper, Connection connection
    ) throws SQLException {
        val preparedStatement = connection.prepareStatement(
                "INSERT INTO event_store (timestamp, handle, aggregate_id, payload) " +
                "SELECT ? AS timestamp, ? AS handle, ? AS aggregate_id, ?::JSONB AS payload " +
                "WHERE NOT EXISTS (SELECT aggregate_id, handle FROM event_store WHERE payload->>'store' = ? AND payload->>'reference' = ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        val payload = new JsonMapper<>(valueType, objectMapper).toJson(value);
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, aggregateId.getValue());
        preparedStatement.setObject(4, trimJsonToEmpty(payload));
        preparedStatement.setString(5, store.getValue());
        preparedStatement.setString(6, reference.getValue());
        return preparedStatement;
    }

    public static PreparedStatement preparedStatementFor(
            Timestamp timestamp, String handle, SingleValue aggregateId, String payload, Connection connection
    ) throws SQLException {
        val preparedStatement = connection.prepareStatement(
                "INSERT INTO event_store (timestamp, handle, aggregate_id, payload) " +
                        "SELECT ? AS timestamp, ? AS handle, ? AS aggregate_id, ?::JSONB AS payload " +
                        "WHERE NOT EXISTS (SELECT aggregate_id, handle FROM event_store WHERE payload->>'store' = ? AND payload->>'reference' = ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        val aggregateIdValue = aggregateId.getValue();
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, aggregateIdValue);
        preparedStatement.setObject(4, trimJsonToEmpty(payload));
        preparedStatement.setString(5, aggregateIdValue);
        preparedStatement.setString(6, handle);
        return preparedStatement;
    }

}