package es.eriktorr.katas.orders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import static java.nio.charset.StandardCharsets.UTF_8;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OrderReceiptApplication.class)
class HealthCheckFeatureTest {

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("Inform about health of the service")
    @Test void
    inform_about_health_of_the_service() {
        webTestClient.get().uri("/management/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("{\"status\" : \"UP\"}");
    }

    @DisplayName("Inform about collected metrics to authorized users")
    @Test void
    inform_about_collected_metrics_to_authorized_users() {
        webTestClient.get().uri("/management/metrics")
                .header("Authorization", "Basic " + accessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("{}");
    }

    @DisplayName("Don't disclose collected metrics to unauthorized users")
    @Test void
    do_not_disclose_collected_metrics_to_unauthorized_users() {
        webTestClient.get().uri("/management/metrics")
                .header("Authorization", "Basic " + accessToken())
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }

    private String accessToken() {
        return Base64Utils.encodeToString(("admin:s3C4E7").getBytes(UTF_8));
    }

}