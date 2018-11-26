package es.eriktorr.katas.orders.infrastructure.common;

import es.eriktorr.katas.orders.domain.common.SingleValue;
import lombok.val;

import javax.sql.rowset.serial.SerialClob;
import java.sql.*;

import static es.eriktorr.katas.orders.domain.common.StringUtils.trimJsonToEmpty;

public final class EventStorePreparedStatementCreator {

    private EventStorePreparedStatementCreator() {}

    public static PreparedStatement preparedStatementFor(
            Timestamp timestamp, String handle, SingleValue aggregateId, String payload, Connection connection
    ) throws SQLException {
        val preparedStatement = connection.prepareStatement(
                "INSERT INTO event_store (timestamp, handle, aggregate_id, payload) " +
                "SELECT ? AS timestamp, ? AS handle, ? AS aggregate_id, ? AS payload FROM DUAL " +
                "WHERE NOT EXISTS (SELECT aggregate_id, handle FROM event_store WHERE aggregate_id = ? AND handle = ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        val aggregateIdValue = aggregateId.getValue();
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, aggregateIdValue);
        preparedStatement.setClob(4, new SerialClob(trimJsonToEmpty(payload)));
        preparedStatement.setString(5, aggregateIdValue);
        preparedStatement.setString(6, handle);
        return preparedStatement;
    }

}