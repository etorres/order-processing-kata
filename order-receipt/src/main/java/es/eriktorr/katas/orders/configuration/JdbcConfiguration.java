package es.eriktorr.katas.orders.configuration;

import com.zaxxer.hikari.HikariDataSource;
import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.json.OrderJsonMapper;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfiguration {

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
    EventStoreRepository eventStoreRepository(JdbcTemplate jdbcTemplate, OrderJsonMapper orderJsonMapper, Clock clock) {
        return new EventStoreRepository(jdbcTemplate, orderJsonMapper, clock);
    }

}