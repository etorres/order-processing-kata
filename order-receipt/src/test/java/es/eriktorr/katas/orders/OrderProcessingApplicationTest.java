package es.eriktorr.katas.orders;

import es.eriktorr.katas.orders.domain.model.Order;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static es.eriktorr.katas.orders.configuration.JmsConfiguration.ORDER_QUEUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = OrderProcessingApplication.class)
class OrderProcessingApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JmsTemplate jmsTemplate;

    @DisplayName("Process Order")
	@Test void
	process_order() {


        // TODO
        jmsTemplate.convertAndSend(ORDER_QUEUE, new Order(UUID.fromString("4472a477-931e-48b7-8bfb-95daa1ad0216")));
        // TODO


        val order = new Order(UUID.fromString("4472a477-931e-48b7-8bfb-95daa1ad0216"));
        webTestClient.post().uri("/orders").contentType(APPLICATION_JSON_UTF8)
                .body(fromObject(order))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/orders/4472a477-931e-48b7-8bfb-95daa1ad0216")
                .expectBody().isEmpty();
	}

}