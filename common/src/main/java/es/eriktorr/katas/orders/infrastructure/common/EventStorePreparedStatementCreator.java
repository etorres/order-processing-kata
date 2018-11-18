package es.eriktorr.katas.orders.infrastructure.common;

import es.eriktorr.katas.orders.domain.common.SingleValue;
import lombok.val;

import javax.sql.rowset.serial.SerialClob;
import java.sql.*;
import java.util.Optional;

public class EventStorePreparedStatementCreator {

    public static PreparedStatement preparedStatementFor(
            Timestamp timestamp, String handle, SingleValue aggregateId, String payload, Connection connection
    ) throws SQLException {
        val preparedStatement = connection.prepareStatement(
                "INSERT INTO event_store (timestamp, handle, aggregate_id, payload) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setString(2, handle);
        preparedStatement.setString(3, aggregateId.getValue());
        preparedStatement.setClob(4, new SerialClob(payloadOrEmpty(payload)));
        return preparedStatement;
    }

    private static char[] payloadOrEmpty(String payload) {
        return Optional.ofNullable(trimToNull(payload)).orElse("{}").toCharArray();
    }

    private static String trimToNull(final String str) {
        final String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    private static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

}