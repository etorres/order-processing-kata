package es.eriktorr.katas.orders.test;

import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.DriverManager;
import java.util.Properties;

public abstract class TruncateDataExtension implements AfterAllCallback {

    private final String datasourceUrl;

    protected TruncateDataExtension(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        val properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", System.getenv("POSTGRES_ROOT_PASSWORD"));
        try (
                val connection = DriverManager.getConnection(datasourceUrl, properties);
                val statement = connection.createStatement()
        ) {
            statement.executeUpdate("TRUNCATE orders RESTART IDENTITY CASCADE");
            statement.executeUpdate("TRUNCATE event_store RESTART IDENTITY CASCADE");
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        }
    }

}