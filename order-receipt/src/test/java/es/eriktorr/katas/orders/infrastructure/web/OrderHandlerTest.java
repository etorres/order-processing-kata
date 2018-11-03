package es.eriktorr.katas.orders.infrastructure.web;

import es.eriktorr.katas.orders.OrderReceiptApplication;
import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.model.*;
import es.eriktorr.katas.orders.infrastructure.web.utils.CreateOrderEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = OrderReceiptApplication.class)
class OrderHandlerTest {

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2018, 11, 3, 14, 48, 17, 242);

    private static final String STORE_ID = "00-396-261";
    private static final String ORDER_REFERENCE = "7158";

    private static final OrderId ORDER_ID = new OrderId("4472a477-931e-48b7-8bfb-95daa1ad0216");
    private static final Order ORDER = new Order(ORDER_ID, new StoreId(STORE_ID), new OrderReference(ORDER_REFERENCE), LOCAL_DATE_TIME);

    @TestConfiguration
    static class OrderHandlerTestConfiguration {
        @Bean
        CreateOrderEventListener createOrderListener() {
            return new CreateOrderEventListener();
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CreateOrderEventListener createOrderEventListener;

    @MockBean
    private OrderIdGenerator orderIdGenerator;

    @MockBean
    private Clock clock;

    @DisplayName("Create a new order")
	@Test void
	create_a_new_order() {
        given(orderIdGenerator.nextOrderId()).willReturn(ORDER_ID);
        given(clock.now()).willReturn(LOCAL_DATE_TIME);

        webTestClient.post().uri("/stores/" + STORE_ID + "/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject("{\"reference\":\"" + ORDER_REFERENCE + "\"}"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/stores/" + STORE_ID + "/orders/" + ORDER_ID)
                .expectBody().isEmpty();

        await().atMost(10L, SECONDS).until(() -> createOrderEventListener.eventReceived(ORDER), equalTo(true));
	}

    @DisplayName("Invalid input parameters will cause error")
    @Test void
    fail_with_bad_request_error_when_input_parameters_are_invalid() {
        given(orderIdGenerator.nextOrderId()).willReturn(ORDER_ID);
        given(clock.now()).willReturn(LOCAL_DATE_TIME);

        webTestClient.post().uri("/stores/" + STORE_ID + "/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject("{}"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().json("{\"title\":\"Bad Request\",\"status\":400,\"detail\":\"Order reference cannot be blank\"}");
    }

}