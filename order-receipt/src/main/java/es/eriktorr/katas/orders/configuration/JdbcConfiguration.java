package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcConfiguration {

    @Bean
    EventStoreRepository eventStoreRepository(JdbcTemplate jdbcTemplate) {
        return new EventStoreRepository(jdbcTemplate);
    }

}