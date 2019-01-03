package es.eriktorr.katas.orders.end_to_end;

import lombok.val;

import java.sql.DriverManager;
import java.util.Properties;

class DatabaseCleaner {

    private static final String DATASOURCE_URL = "jdbc:postgresql://localhost:5432/orders_db";

    void cleanUp(String orderId) throws Exception {
        val properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", System.getenv("POSTGRES_ROOT_PASSWORD"));
        try (
                val connection = DriverManager.getConnection(DATASOURCE_URL, properties);
                val statement = connection.createStatement()
        ) {
            statement.executeUpdate(String.format("DELETE FROM orders WHERE id = '%s'", orderId));
            statement.executeUpdate(String.format("DELETE FROM event_store WHERE aggregate_id = '%s'", orderId));
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        }
    }

}