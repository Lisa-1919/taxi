package com.modsen.driver.e2e.step;

import com.modsen.driver.util.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Component
public class DriverSteps {

    private Response response;
    private final String baseUri = "http://localhost:8081/";
    private String payload;

    @Given("the driver with ID {int} exists")
    public void theDriverWithIdExists(int id) {
        RestAssured.given()
                .baseUri(baseUri)
                .get(TestUtils.DRIVER_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @When("I send a GET request for a driver to {string}")
    public void iSendAGetRequestForDriverTo(String endpoint) {
        response = RestAssured.given()
                .baseUri(baseUri)
                .get(endpoint);
    }

    @Given("I have a valid driver payload")
    public void iHaveAValidDriverPayload() {
        payload = loadPayload("valid-driver.json");
    }

    @Given("I have a valid add driver payload")
    public void iHaveAValidAddDriverPayload() {
        payload = loadPayload("valid-add-driver.json");
    }

    @When("I send a POST request for a driver to {string} with the payload")
    public void iSendAPostRequestForDriverToWithThePayload(String endpoint) {
        response = RestAssured.given()
                .baseUri(baseUri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .post(endpoint);
    }

    @When("I send a DELETE request for a driver to {string}")
    public void iSendADeleteRequestForDriverTo(String endpoint) {
        response = RestAssured.given()
                .baseUri(baseUri)
                .delete(endpoint);
    }

    @Then("the response status code for driver should be {int}")
    public void theResponseStatusCodeForDriverShouldBe(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("the response body for driver should contain {string}: {string}")
    public void theResponseBodyForDriverShouldContain(String key, String value) {
        response.then().body(key, equalTo(value));
    }

    @Then("the response body for driver should contain {string}: {int}")
    public void theResponseBodyForDriverShouldContain(String key, int value) {
        response.then().body(key, equalTo(value));
    }

    @Then("the response body for driver should contain {string}")
    public void theResponseBodyForDriverShouldContain(String key) {
        response.then().body(key, notNullValue());
    }

    @Then("the driver with ID {int} should no longer exist")
    public void theDriverWithIdShouldNoLongerExist(int id) {
        RestAssured.given()
                .baseUri(baseUri)
                .get(TestUtils.DRIVER_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @When("I send a PUT request for a driver to {string} with the payload")
    public void iSendAPutRequestForDriverToWithThePayload(String endpoint) {
        response = RestAssured.given()
                .baseUri(baseUri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .put(endpoint);
    }

    @Given("I have an invalid driver payload")
    public void iHaveAnInvalidDriverPayload() {
        payload = loadPayload("invalid-driver.json");
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
