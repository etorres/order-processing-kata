package es.eriktorr.katas.orders.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.infrastructure.database.EventStorePreparedStatementCreator;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
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
    EventStorePreparedStatementCreator eventStorePreparedStatementCreator(ObjectMapper objectMapper) {
        return new EventStorePreparedStatementCreator(objectMapper);
    }

    @Bean
    EventStoreRepository eventStoreRepository(JdbcTemplate jdbcTemplate, EventStorePreparedStatementCreator eventStorePreparedStatementCreator, Clock clock) {
        return new EventStoreRepository(jdbcTemplate, eventStorePreparedStatementCreator, clock);
    }

}