package es.eriktorr.katas.orders.infrastructure.web;

import es.eriktorr.katas.orders.OrderReceiptApplication;
import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.model.*;
import es.eriktorr.katas.orders.infrastructure.web.utils.OrderCreatedEventListener;
import es.eriktorr.katas.orders.test.TruncateDataExtension;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static es.eriktorr.katas.orders.infrastructure.web.OrderHandlerTest.PrimaryConfiguration;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Tag("integration")
@DisplayName("Orders HTTP handler")
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = { OrderReceiptApplication.class, PrimaryConfiguration.class }, properties = {
        "spring.flyway.enabled=true",
        "spring.flyway.schemas=test_order_receipt",
        "spring.flyway.locations=filesystem:${TEST_PROJECT_HOME:-/tmp}/docker/db/migration"
})
@ExtendWith(TruncateDataExtension.class)
class OrderHandlerTest {

    private static final String STORE_ID = "00-396-261";
    private static final String ORDER_REFERENCE = "7158";
    private static final String CREATED_AT = "2018-11-03T14:48:17.000000242";

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.parse(CREATED_AT, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private static final OrderId ORDER_ID = new OrderId("4472a477-931e-48b7-8bfb-95daa1ad0216");

    @TestConfiguration
    static class PrimaryConfiguration {
        @Bean
        OrderCreatedEventListener createOrderListener() {
            return new OrderCreatedEventListener();
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OrderCreatedEventListener orderCreatedEventListener;

    @MockBean
    private OrderIdGenerator orderIdGenerator;

    @MockBean
    private Clock clock;

    @DisplayName("Create a new order")
    @Test void
    create_a_new_order() {
        val timestamp = Timestamp.valueOf(LOCAL_DATE_TIME.plus(1673L, ChronoUnit.MILLIS));
        val order = new Order(ORDER_ID, new StoreId(STORE_ID), new OrderReference(ORDER_REFERENCE), LOCAL_DATE_TIME);
        val orderCreatedEvent = OrderCreatedEvent.build(Long.MIN_VALUE, timestamp.toLocalDateTime(), order);

        given(orderIdGenerator.nextOrderId()).willReturn(ORDER_ID);
        given(clock.currentTimestamp()).willReturn(timestamp);

        webTestClient.post().uri("/stores/" + STORE_ID + "/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject("{\"reference\":\"" + ORDER_REFERENCE + "\",\"createdAt\":\"" + CREATED_AT + "\"}"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/stores/" + STORE_ID + "/orders/" + ORDER_ID)
                .expectBody().isEmpty();

        await().atMost(10L, SECONDS).until(() -> orderCreatedEventListener.eventReceived(orderCreatedEvent), equalTo(true));
    }

    @DisplayName("Invalid input parameters will cause error")
    @Test void
    fail_with_bad_request_error_when_input_parameters_are_invalid() {
        given(orderIdGenerator.nextOrderId()).willReturn(ORDER_ID);

        webTestClient.post().uri("/stores/" + STORE_ID + "/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject("{}"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.type").isEqualTo("https://example.org/invalid-parameters")
                .jsonPath("$.title").isEqualTo("Invalid Parameters")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.detail").isEqualTo("The request violates one or several constraints")
                .jsonPath("$.storeId").isEqualTo(STORE_ID)
                .jsonPath("$.violations[0].field").isEqualTo("createdAt")
                .jsonPath("$.violations[0].message").isEqualTo("Creation date & time is needed")
                .jsonPath("$.violations[1].field").isEqualTo("orderReference")
                .jsonPath("$.violations[1].message").isEqualTo("Order reference cannot be blank");
    }

    @DisplayName("Handle duplicate orders")
    @Test void
    fail_to_create_an_order_with_duplicate_reference_in_the_same_store() {
        val orderId_1 = new OrderId("10593d3e-7fce-40f1-9155-1f8dbb74a27e");
        val orderId_2 = new OrderId("9763ad60-76ce-45cc-b597-5858352a49da");

        val reference = "9666";
        val timestamp = Timestamp.valueOf(LOCAL_DATE_TIME.plus(8041L, ChronoUnit.MILLIS));

        given(orderIdGenerator.nextOrderId()).willReturn(orderId_1, orderId_2);
        given(clock.currentTimestamp()).willReturn(timestamp, timestamp);

        webTestClient.post().uri("/stores/" + STORE_ID + "/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject("{\"reference\":\"" + reference + "\",\"createdAt\":\"" + CREATED_AT + "\"}"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/stores/" + STORE_ID + "/orders/" + orderId_1)
                .expectBody().isEmpty();

        webTestClient.post().uri("/stores/" + STORE_ID + "/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject("{\"reference\":\"" + reference + "\",\"createdAt\":\"" + CREATED_AT + "\"}"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.type").isEqualTo("https://example.org/conflict")
                .jsonPath("$.title").isEqualTo("Request conflicted with the order's state")
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.detail").isEqualTo("The request could not be completed due to a conflict with the current state of the target order. " +
                "Conflicts are most likely to occur when there is a duplicate order created by an earlier request.")
                .jsonPath("$.storeId").isEqualTo(STORE_ID)
                .jsonPath("$.errorMessage").isEqualTo("cannot create an order created event in the database");
    }

}