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
        val preparedStatement = connection.prepareStatement("INSERT INTO event_store (timestamp, handle, aggregate_id, payload) VALUES (?, ?, ?, ?::JSONB)",
                Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, value.getOrderId().getValue());
        preparedStatement.setObject(4, payloadFrom(value, objectMapper));
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