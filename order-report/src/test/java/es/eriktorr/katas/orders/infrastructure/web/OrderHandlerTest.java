package es.eriktorr.katas.orders.infrastructure.web;

import es.eriktorr.katas.orders.OrderReportApplication;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.OrderReference;
import es.eriktorr.katas.orders.domain.model.StoreId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_UTF8;

@Tag("integration")
@DisplayName("Orders HTTP handler")
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = OrderReportApplication.class, properties = {
        "spring.flyway.enabled=true",
        "spring.flyway.locations=filesystem:${TEST_PROJECT_HOME:-/tmp}/docker/db/migration,classpath:/db/migration"
})
class OrderHandlerTest {

    private static final String STORE_ID = "00-396-261";
    private static final String ORDER_ID = "4472a477-931e-48b7-8bfb-95daa1ad0216";

    private static final String NONEXISTENT_STORE_ID = "8c-2e8-98e";
    private static final String NONEXISTENT_ORDER_ID = "e17b5774-13ad-430d-8be8-5fcba7b27fee";

    private static final Order ORDER = new Order(
            new OrderId(ORDER_ID),
            new StoreId(STORE_ID),
            new OrderReference("7158"),
            LocalDateTime.parse("2018-11-03T14:48:17.242", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("Find an order by its Id")
    @Test void
    find_an_order_by_id() {
        webTestClient.get().uri("/stores/" + STORE_ID + "/orders/" + ORDER_ID).accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(Order.class).isEqualTo(ORDER);
    }

    @DisplayName("Nonexistent store will cause error")
    @Test void
    fail_with_not_found_http_status_code_when_store_does_not_exist() {
        webTestClient.get().uri("/stores/" + NONEXISTENT_STORE_ID + "/orders/" + ORDER_ID).accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON_UTF8)
                .expectBody()
                .jsonPath("$.type").isEqualTo("https://example.org/order-not-found")
                .jsonPath("$.title").isEqualTo("Order Not Found")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.storeId").isEqualTo(NONEXISTENT_STORE_ID)
                .jsonPath("$.orderId").isEqualTo(ORDER_ID);
    }

    @DisplayName("Nonexistent order will cause error")
    @Test void
    fail_with_not_found_http_status_code_when_order_does_not_exist() {
        webTestClient.get().uri("/stores/" + STORE_ID + "/orders/" + NONEXISTENT_ORDER_ID).accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON_UTF8)
                .expectBody()
                .jsonPath("$.type").isEqualTo("https://example.org/order-not-found")
                .jsonPath("$.title").isEqualTo("Order Not Found")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.detail").isEqualTo("The order cannot be found in the specified store")
                .jsonPath("$.storeId").isEqualTo(STORE_ID)
                .jsonPath("$.orderId").isEqualTo(NONEXISTENT_ORDER_ID);
    }

}