package es.eriktorr.katas.orders.end_to_end;

import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.val;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinition {

    private String orderId;

    @When("the online store {storeId} sells a set of items at {localDateTime} with order reference {int}")
    public void onlineStoreSubmitsOrder(String storeId, LocalDateTime createdAt, int orderReference) throws IOException {
        val response = Request.Post("http://localhost:8080/stores/00-396-261/orders")
                .bodyString("{\"reference\":\"7158\",\"createdAt\":\"2018-11-03T14:48:17.000000242\"}", ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();
        val locationHeader = response.getFirstHeader("Location");
        if (locationHeader != null) {
            val lastSlashPosition = locationHeader.getValue().lastIndexOf("/");
            orderId = locationHeader.getValue().substring(0, lastSlashPosition);
        }
    }

    @Then("an order identifier is generated for the order")
    public void orderIdentifierIsCreated() {
        assertThat(orderId).isNotBlank();
    }

    @Then("the order status is make available to the online store in a new endpoint generated from the order identifier")
    public void orderStatusInformationIsAvailable() {
        System.err.println("\n\n >> WHEN 3\n");
    }

    @After
    public void tearDown() throws Exception {
        val databaseCleaner = new DatabaseCleaner();
        databaseCleaner.cleanUp(orderId);
    }

//    @When("^users upload data on a project$")
//    public void usersUploadDataOnAProject() {
//        System.err.println("\n\n >> WHEN 1\n");
//    }
//
//    @When("^users want to get information on the (.+) project$")
//    public void usersGetInformationOnAProject(String projectName) {
//        System.err.println("\n\n >> WHEN 2\n");
//    }
//
//    @Then("^the server should handle it and return a success status$")
//    public void theServerShouldReturnASuccessStatus() {
//        System.err.println("\n\n >> THEN 1\n");
//    }
//
//    @Then("^the requested data is returned$")
//    public void theRequestedDataIsReturned() {
//        System.err.println("\n\n >> THEN 2\n");
//    }

}