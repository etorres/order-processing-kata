package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.json.OrderJsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcConfiguration {

    @Bean
    EventStoreRepository eventStoreRepository(JdbcTemplate jdbcTemplate, OrderJsonMapper orderJsonMapper, Clock clock) {
        return new EventStoreRepository(jdbcTemplate, orderJsonMapper, clock);
    }

}