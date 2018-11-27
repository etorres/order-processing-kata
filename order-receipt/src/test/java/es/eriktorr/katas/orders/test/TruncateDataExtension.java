package es.eriktorr.katas.orders.test;

import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.DriverManager;
import java.util.Properties;

public class TruncateDataExtension implements AfterAllCallback {

    private static final String DATASOURCE_URL = "jdbc:postgresql://localhost:5432/orders_db?currentSchema=test_order_receipt";

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        val properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", System.getenv("POSTGRES_ROOT_PASSWORD"));
        try (
                val connection = DriverManager.getConnection(DATASOURCE_URL, properties);
                val statement = connection.createStatement()
        ) {
            statement.executeUpdate("TRUNCATE orders RESTART IDENTITY CASCADE");
            statement.executeUpdate("TRUNCATE event_store RESTART IDENTITY CASCADE");
            connection.commit();
        }
    }

}