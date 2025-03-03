package com.modsen.rating.e2e.step;

import com.modsen.rating.util.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.notNullValue;

public class RateSteps {

    private Response response;
    private final String baseUri = "http://localhost:8765";
    private String payload;

    @Given("the rate with ID {int} exists")
    public void theRateWithIdExists(int id) {
        BaseSteps.request
                .baseUri(baseUri)
                .get(TestUtils.RATE_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Given("I have a valid rate payload")
    public void iHaveAValidRatePayload() {
        payload = loadPayload("add-rate.json");
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

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String key) {
        response.then().body(key, notNullValue());
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

