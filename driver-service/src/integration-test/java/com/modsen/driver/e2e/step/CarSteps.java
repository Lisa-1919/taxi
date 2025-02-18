package com.modsen.driver.e2e.step;

import com.modsen.driver.util.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CarSteps {

    private Response response;
    private final String baseUri = "http://localhost:8765/";
    private String payload;

    @Given("the car with ID {int} exists")
    public void theCarWithIdExists(int id) {
        BaseSteps.request
                .baseUri(baseUri)
                .get(TestUtils.CAR_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Given("I have a valid car payload")
    public void iHaveAValidCarPayload() {
        payload = loadPayload("valid-add-car.json");
    }

    @Given("I have an updated car payload")
    public void iHaveAnUpdatedCarPayload() {
        payload = loadPayload("valid-edit-car.json");
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = BaseSteps.request
                .baseUri(baseUri)
                .get(endpoint);
    }

    @When("I send a POST request to {string} with the payload")
    public void iSendAPostRequestToWithThePayload(String endpoint) {
        response = BaseSteps.request
                .baseUri(baseUri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .post(endpoint);
    }

    @When("I send a PUT request to {string} with the payload")
    public void iSendAPutRequestToWithThePayload(String endpoint) {
        response = BaseSteps.request
                .baseUri(baseUri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .put(endpoint);
    }

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        response = BaseSteps.request
                .baseUri(baseUri)
                .delete(endpoint);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("the response body should contain {string}: {string}")
    public void theResponseBodyShouldContain(String key, String value) {
        response.then().body(key, equalTo(value));
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String key) {
        response.then().body(key, notNullValue());
    }

    @Then("the car with ID {int} should no longer exist")
    public void theCarWithIdShouldNoLongerExist(int id) {
        BaseSteps.request
                .baseUri(baseUri)
                .get(TestUtils.CAR_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private String loadPayload(String filename) {
        try {
            Path filePath = Paths.get("src/integration-test/resources/payloads", filename);
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load payload: " + filename, e);
        }
    }

}
