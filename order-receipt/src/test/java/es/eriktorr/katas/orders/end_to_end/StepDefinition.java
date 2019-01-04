package es.eriktorr.katas.orders.end_to_end;

import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.val;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class StepDefinition {

    private static Pattern IS_GUID = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private String storeId;
    private String orderId;

    @When("the online store {storeId} sells a set of items at {localDateTime} with order reference {int}")
    public void onlineStoreSubmitsOrder(String storeId, String createdAt, int orderReference) throws IOException {
        val response = Request.Post(String.format("http://localhost:8080/stores/%s/orders", storeId))
                .bodyString(String.format("{\"reference\":\"%s\",\"createdAt\":\"%s\"}", orderReference, createdAt), ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();
        val locationHeader = response.getFirstHeader("Location");
        this.storeId = storeId;
        this.orderId = orderIdFrom(locationHeader);
    }

    private String orderIdFrom(Header locationHeader) {
        if (locationHeader == null) return null;
        val lastSlashPosition = locationHeader.getValue().lastIndexOf("/");
        val extractedOrderId = locationHeader.getValue().substring(lastSlashPosition + 1);
        return IS_GUID.matcher(extractedOrderId).matches() ? extractedOrderId : null;
    }

    @Then("an order identifier is generated for the order")
    public void orderIdentifierIsCreated() {
        assertThat(orderId).isNotBlank();
    }

    @Then("the order status is make available to the online store in a new endpoint generated from the order identifier")
    public void orderStatusInformationIsAvailable() {
        await().atMost(10L, SECONDS).untilAsserted(() -> assertThat(
                fetchOrderStatus(storeId, orderId).getStatusLine().getStatusCode()).isEqualTo(200L)
        );
    }

    private HttpResponse fetchOrderStatus(String storeId, String orderId) throws IOException {
        return Request.Get(String.format("http://localhost:8000/stores/%s/orders/%s", storeId, orderId))
                .connectTimeout(1000)
                .socketTimeout(1000)
                .execute()
                .returnResponse();
    }

    @After
    public void tearDown() throws Exception {
        if (orderId != null) {
            val databaseCleaner = new DatabaseCleaner();
            databaseCleaner.cleanUp(orderId);
        }
    }

}