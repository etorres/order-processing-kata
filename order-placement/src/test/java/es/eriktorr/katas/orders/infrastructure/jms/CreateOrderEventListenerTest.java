package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.common.DomainEventMetadata;
import es.eriktorr.katas.orders.domain.model.*;
import es.eriktorr.katas.orders.infrastructure.jms.utils.OrderEventSender;
import es.eriktorr.katas.orders.OrderPlacementApplication;
import lombok.val;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Tag("integration")
@DisplayName("Created order event listener")
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(classes = OrderPlacementApplication.class)
class CreateOrderEventListenerTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2018, 11, 11, 12, 27, 34, 897);
    private static final OrderId ORDER_ID = new OrderId("4472a477-931e-48b7-8bfb-95daa1ad0216");

    @TestConfiguration
    static class DummyTestConfiguration {
        @Bean
        OrderEventSender orderEventSender(JmsTemplate jmsTemplate, @Value("${orders.queue.name}") final String ordersQueueName) {
            return new OrderEventSender(jmsTemplate, new ActiveMQQueue(ordersQueueName));
        }
    }

    @Autowired
    private OrderEventSender orderEventSender;

    @DisplayName("Handle created order event")
    @Test void
    handle_created_order_event() {
        val orderCreatedEvent = new OrderCreatedEvent(
                23L,
                new DomainEventMetadata(CREATED_AT.plus(984L, ChronoUnit.MILLIS), 1),
                "order.created",
                ORDER_ID,
                new Order(
                        ORDER_ID,
                        new StoreId("00-396-261"),
                        new OrderReference("7158"),
                        CREATED_AT
                )
        );

        orderEventSender.send(orderCreatedEvent);

        
    }

}