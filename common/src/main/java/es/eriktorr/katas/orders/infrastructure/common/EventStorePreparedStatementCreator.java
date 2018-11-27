package es.eriktorr.katas.orders.infrastructure.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.eriktorr.katas.orders.domain.common.SingleValue;
import es.eriktorr.katas.orders.domain.model.WithOrderInformation;
import lombok.val;

import java.sql.*;

import static es.eriktorr.katas.orders.domain.common.StringUtils.EMPTY_JSON;
import static es.eriktorr.katas.orders.domain.common.StringUtils.trimJsonToEmpty;

public final class EventStorePreparedStatementCreator {

    private EventStorePreparedStatementCreator() {}

    public static <T extends WithOrderInformation> PreparedStatement preparedStatementFor(
            Timestamp timestamp, String handle, T value, Class<T> valueType, ObjectMapper objectMapper, Connection connection
    ) throws SQLException {
        val preparedStatement = connection.prepareStatement(
                "INSERT INTO event_store (timestamp, handle, aggregate_id, payload) " +
                        "SELECT ? AS timestamp, ? AS handle, ? AS aggregate_id, ?::JSONB AS payload " +
                        "WHERE NOT EXISTS " +
                        "(SELECT aggregate_id, handle FROM event_store WHERE payload->>'store' = ? AND payload->>'reference' = ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, value.getOrderId().getValue());
        preparedStatement.setObject(4, payloadFrom(value, valueType, objectMapper));
        preparedStatement.setString(5, value.getStoreId().getValue());
        preparedStatement.setString(6, value.getOrderReference().getValue());
        return preparedStatement;
    }

    public static PreparedStatement preparedStatementFor(
            Timestamp timestamp, String handle, SingleValue aggregateId, Connection connection
    ) throws SQLException {
        val preparedStatement = connection.prepareStatement(
                "INSERT INTO event_store (timestamp, handle, aggregate_id, payload) " +
                        "SELECT ? AS timestamp, ? AS handle, ? AS aggregate_id, ?::JSONB AS payload " +
                        "WHERE NOT EXISTS " +
                        "(SELECT aggregate_id FROM event_store WHERE aggregate_id = ? AND handle = ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        val aggregateIdValue = aggregateId.getValue();
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, aggregateIdValue);
        preparedStatement.setObject(4, EMPTY_JSON);
        preparedStatement.setString(5, aggregateIdValue);
        preparedStatement.setString(6, handle);
        return preparedStatement;
    }

    private static <T> String payloadFrom(T value, Class<T> valueType, ObjectMapper objectMapper) {
        val payload = new JsonMapper<>(valueType, objectMapper).toJson(value);
        return trimJsonToEmpty(payload);
    }

}