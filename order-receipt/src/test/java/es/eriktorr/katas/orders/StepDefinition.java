package es.eriktorr.katas.orders;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepDefinition {

    @When("^users upload data on a project$")
    public void usersUploadDataOnAProject() {
        System.err.println("\n\n >> WHEN 1\n");
    }

    @When("^users want to get information on the (.+) project$")
    public void usersGetInformationOnAProject(String projectName) {
        System.err.println("\n\n >> WHEN 2\n");
    }

    @Then("^the server should handle it and return a success status$")
    public void theServerShouldReturnASuccessStatus() {
        System.err.println("\n\n >> THEN 1\n");
    }

    @Then("^the requested data is returned$")
    public void theRequestedDataIsReturned() {
        System.err.println("\n\n >> THEN 2\n");
    }

}