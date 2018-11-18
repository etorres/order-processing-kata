package es.eriktorr.katas.orders;

import es.eriktorr.katas.orders.configuration.JmsConfiguration;
import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.services.OrderPlacer;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import es.eriktorr.katas.orders.infrastructure.jms.OrderCreatedEventListener;
import es.eriktorr.katas.orders.infrastructure.jms.OrderPlacedEventSender;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication(scanBasePackageClasses = JmsConfiguration.class)
public class OrderPlacementApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPlacementApplication.class, args);
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
    OrderPlacedEventSender orderPlacedEventSender(
            JmsTemplate jmsTemplate,
            @Value("${order.placed.event.queue.name}") final String placedOrdersQueueName
    ) {
        return new OrderPlacedEventSender(jmsTemplate, new ActiveMQQueue(placedOrdersQueueName));
    }

    @Bean
    OrderPlacer orderPlacer(EventStoreRepository eventStoreRepository, OrderPlacedEventSender orderPlacedEventSender) {
        return new OrderPlacer(eventStoreRepository, orderPlacedEventSender);
    }

    @Bean
    OrderCreatedEventListener orderCreatedEventListener(OrderPlacer orderPlacer) {
        return new OrderCreatedEventListener(orderPlacer);
    }

}