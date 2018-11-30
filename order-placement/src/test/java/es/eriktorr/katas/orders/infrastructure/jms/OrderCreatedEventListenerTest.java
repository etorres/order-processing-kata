package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.OrderPlacementApplication;
import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.events.DomainEventMetadata;
import es.eriktorr.katas.orders.domain.model.*;
import es.eriktorr.katas.orders.infrastructure.jms.utils.OrderCreatedEventSender;
import es.eriktorr.katas.orders.infrastructure.jms.utils.OrderPlacedEventListener;
import es.eriktorr.katas.orders.test.TruncateOrderPlacementData;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static es.eriktorr.katas.orders.infrastructure.jms.OrderCreatedEventListenerTest.PrimaryConfiguration;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.mockito.BDDMockito.given;

@Tag("integration")
@DisplayName("Created order event listener")
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(classes = { OrderPlacementApplication.class, PrimaryConfiguration.class }, properties = {
        "spring.flyway.enabled=true",
        "spring.flyway.locations=filesystem:${TEST_PROJECT_HOME:-/tmp}/docker/db/migration"
})
@ExtendWith(TruncateOrderPlacementData.class)
class OrderCreatedEventListenerTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2018, 11, 11, 12, 27, 34, 897);
    private static final OrderId ORDER_ID = new OrderId("4472a477-931e-48b7-8bfb-95daa1ad0216");

    private static final Order ORDER = new Order(
            ORDER_ID,
            new StoreId("00-396-261"),
            new OrderReference("7158"),
            CREATED_AT
    );

    private static final OrderCreatedEvent ORDER_CREATED_EVENT = new OrderCreatedEvent(
            23L,
            new DomainEventMetadata(CREATED_AT.plus(209L, ChronoUnit.MILLIS), 1),
            "order.created",
            ORDER_ID,
            ORDER
    );

    private static final LocalDateTime PROCESSED_AT = CREATED_AT.plus(984L, ChronoUnit.MILLIS);

    private static final OrderPlacedEvent ORDER_PLACED_EVENT = OrderPlacedEvent.build(
            Long.MIN_VALUE, PROCESSED_AT, ORDER
    );

    @TestConfiguration
    static class PrimaryConfiguration {
        @Bean
        OrderCreatedEventSender orderCreatedEventSender(
                JmsTemplate jmsTemplate,
                @Value("${order.created.event.queue.name}") final String orderCreatedQueueName
        ) {
            return new OrderCreatedEventSender(jmsTemplate, new ActiveMQQueue(orderCreatedQueueName));
        }

        @Bean
        OrderPlacedEventListener orderPlacedEventListener() {
            return new OrderPlacedEventListener();
        }
    }

    @Autowired
    private OrderCreatedEventSender orderCreatedEventSender;

    @Autowired
    private OrderPlacedEventListener orderPlacedEventListener;

    @MockBean
    private Clock clock;

    @DisplayName("Handle created order event")
    @Test void
    handle_created_order_event() {
        given(clock.currentTimestamp()).willReturn(Timestamp.valueOf(PROCESSED_AT));

        orderCreatedEventSender.send(ORDER_CREATED_EVENT);

        await().atMost(10L, SECONDS).untilAsserted(() -> assertThat(orderPlacedEventListener.eventReceived(ORDER_PLACED_EVENT)).isTrue());
    }

    @DisplayName("Handle duplicate order created events")
    @Test void
    ignore_an_order_with_duplicate_reference_in_the_same_store() {
        val timestamp = Timestamp.valueOf(PROCESSED_AT);
        val orderId = new OrderId("5e1e8a3a-ad1b-46f1-aa94-d0b1d562e087");
        val order = new Order(
                orderId,
                new StoreId("00-396-261"),
                new OrderReference("4478"),
                CREATED_AT
        );
        val orderCreatedEvent = new OrderCreatedEvent(
                15L,
                new DomainEventMetadata(CREATED_AT.plus(183L, ChronoUnit.MILLIS), 1),
                "order.created",
                orderId,
                order
        );
        val orderPlacedEvent = OrderPlacedEvent.build(
                Long.MAX_VALUE, PROCESSED_AT, order
        );

        given(clock.currentTimestamp()).willReturn(timestamp, timestamp);

        orderCreatedEventSender.send(orderCreatedEvent);
        orderCreatedEventSender.send(orderCreatedEvent);

        with().pollDelay(2L, SECONDS)
                .and().atMost(10L, SECONDS)
                .untilAsserted(() -> assertThat(orderPlacedEventListener.onlyReceived(orderPlacedEvent)).isTrue());
    }

}