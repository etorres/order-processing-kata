package es.eriktorr.katas.orders.configuration;

import com.zaxxer.hikari.HikariDataSource;
import es.eriktorr.katas.orders.infrastructure.database.OrdersRepository;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
class JdbcConfiguration {

    @Bean
    DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    OrdersRepository ordersRepository(JdbcTemplate jdbcTemplate) {
        return new OrdersRepository(jdbcTemplate);
    }

}