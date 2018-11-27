package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.OrderReportApplication;
import es.eriktorr.katas.orders.domain.events.DomainEventMetadata;
import es.eriktorr.katas.orders.domain.model.*;
import es.eriktorr.katas.orders.infrastructure.database.OrdersRepository;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static es.eriktorr.katas.orders.infrastructure.jms.OrderPlacedEventListenerTest.PrimaryConfiguration;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@Tag("integration")
@DisplayName("Placed order event listener")
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(classes = { OrderReportApplication.class, PrimaryConfiguration.class }, properties = {
        "spring.flyway.enabled=true",
        "spring.flyway.locations=filesystem:${TEST_PROJECT_HOME:-/tmp}/docker/db/migration"
})
class OrderPlacedEventListenerTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2018, 11, 11, 12, 27, 34);
    private static final OrderId ORDER_ID = new OrderId("b2cf8a22-d561-453d-b87d-994f2fa720a0");
    private static final StoreId STORE_ID = new StoreId("0b-b50-c57");

    private static final Order ORDER = new Order(
            ORDER_ID,
            STORE_ID,
            new OrderReference("857C"),
            CREATED_AT
    );

    private static final OrderPlacedEvent ORDER_PLACED_EVENT = new OrderPlacedEvent(
            23L,
            new DomainEventMetadata(CREATED_AT.plus(209L, ChronoUnit.MILLIS), 1),
            "order.created",
            ORDER_ID,
            ORDER
    );

    @TestConfiguration
    static class PrimaryConfiguration {
        @Bean
        OrderPlacedEventSender orderPlacedEventSender(
                JmsTemplate jmsTemplate,
                @Value("${order.placed.event.queue.name}") final String orderPlacedQueueName
        ) {
            return new OrderPlacedEventSender(jmsTemplate, new ActiveMQQueue(orderPlacedQueueName));
        }
    }

    @Autowired
    private OrderPlacedEventSender orderPlacedEventSender;

    @Autowired
    private OrdersRepository ordersRepository;

    @DisplayName("Handle placed order event")
    @Test void
    handle_placed_order_event() {
        orderPlacedEventSender.send(ORDER_PLACED_EVENT);

        await().atMost(10L, SECONDS).until(() -> ordersRepository.findOrderBy(STORE_ID, ORDER_ID).block(Duration.ofSeconds(1L)), equalTo(ORDER));
    }

}