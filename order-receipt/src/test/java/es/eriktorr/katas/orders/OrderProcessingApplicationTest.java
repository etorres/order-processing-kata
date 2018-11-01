package es.eriktorr.katas.orders;

import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = OrderProcessingApplication.class)
class OrderProcessingApplicationTest {

    private static final String STORE_ID = "00-396-261";
    private static final String ORDER_ID = "4472a477-931e-48b7-8bfb-95daa1ad0216";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderIdGenerator orderIdGenerator;

    @DisplayName("Process Order")
	@Test void
	process_order() {
        given(orderIdGenerator.nextOrderId()).willReturn(new OrderId(ORDER_ID));

        webTestClient.post().uri("/stores/" + STORE_ID + "/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject("{\"reference\":\"7158\"}"))
                .exchange()
//                .expectStatus().isCreated()
//                .expectHeader().valueEquals("Location", "/stores/" + STORE_ID + "/orders/" + ORDER_ID)
                .expectBody().isEmpty();
	}

    @DisplayName("Input Parameters Validation")
    @Test void
    fail_with_bad_request_error_when_input_parameters_are_invalid() {
        given(orderIdGenerator.nextOrderId()).willReturn(new OrderId(ORDER_ID));

        webTestClient.post().uri("/stores/" + STORE_ID + "/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject("{}"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().json("{\"title\":\"Bad Request\",\"status\":400,\"detail\":\"Order reference cannot be blank\"}");
    }

}