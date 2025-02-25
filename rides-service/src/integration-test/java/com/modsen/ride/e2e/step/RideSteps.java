package com.modsen.ride.e2e.step;

import com.modsen.ride.util.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpHead;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RideSteps {

    private Response response;
    private final String baseUri = "http://localhost:8765";
    private String payload;
    private String accessToken;

    @Given("I authenticate as {string} with password {string}")
    public void iAuthenticateAs(String username, String password) {
        response = given()
                .contentType("application/json")
                .body("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
                .post(baseUri + "/api/v1/account/login");

        response.then().statusCode(200);
        accessToken = response.jsonPath().getString("access_token");
        assertNotNull(accessToken, "Access token must not be null");
    }

    @Given("the ride with ID {int} exists")
    public void theRideWithIdExists(int id) {
        given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .baseUri(baseUri)
                .get(TestUtils.RIDE_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Given("I have a valid ride payload")
    public void iHaveAValidRidePayload() {
        payload = loadPayload("add-ride.json");
    }

    @Given("I have a valid status update payload")
    public void iHaveAValidStatusUpdatePayload() {
        payload = loadPayload("change-status.json");
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .baseUri(baseUri)
                .get(endpoint);
    }

    @When("I send a POST request to {string} with the payload")
    public void iSendAPostRequestToWithThePayload(String endpoint) {
        response = given()
                .baseUri(baseUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .post(endpoint);
    }

    @When("I send a PUT request to {string} with the payload")
    public void iSendAPutRequestToWithThePayload(String endpoint) {
        response = given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .baseUri(baseUri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .put(endpoint);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String key) {
        response.then().body(key, notNullValue());
    }

    @Then("the response body should contain {string}: {string}")
    public void theResponseBodyShouldContain(String key, String value) {
        response.then().body(key, equalTo(value));
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
