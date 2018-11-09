package es.eriktorr.katas.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.eriktorr.katas.orders.configuration.WebSecurityConfiguration;
import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import es.eriktorr.katas.orders.domain.service.OrderReceiver;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.jms.OrderEventSender;
import es.eriktorr.katas.orders.infrastructure.json.OrderJsonMapper;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication(scanBasePackageClasses = { WebSecurityConfiguration.class })
public class OrderReceiptApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderReceiptApplication.class, args);
    }

    @Configuration
    @Profile("test")
    @ComponentScan(lazyInit = true)
    static class LocalConfig {
    }

    @Bean
    Clock clock() {
        return new Clock();
    }

    @Bean
    OrderIdGenerator orderIdGenerator() {
        return new OrderIdGenerator();
    }

    @Bean
    OrderEventSender orderEventSender(JmsTemplate jmsTemplate, @Value("${orders.queue.name}") final String ordersQueueName) {
        return new OrderEventSender(jmsTemplate, new ActiveMQQueue(ordersQueueName));
    }

    @Bean
    OrderReceiver orderReceiver(EventStoreRepository eventStoreRepository, OrderEventSender orderEventSender) {
        return new OrderReceiver(eventStoreRepository, orderEventSender);
    }

    @Bean
    OrderJsonMapper orderJsonMapper(ObjectMapper objectMapper) {
        return new OrderJsonMapper(objectMapper);
    }

}