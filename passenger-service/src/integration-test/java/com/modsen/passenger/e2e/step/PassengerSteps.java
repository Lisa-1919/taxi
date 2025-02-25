package com.modsen.passenger.e2e.step;

import com.modsen.passenger.util.TestUtils;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PassengerSteps {

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
        System.out.println(accessToken);
        assertNotNull(accessToken, "Access token must not be null");
    }

    @Given("the passenger with ID {string} exists")
    public void thePassengerWithIdExists(String id) {
        given()
                .baseUri(baseUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .get(TestUtils.PASSENGER_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Given("I have a valid passenger payload")
    public void iHaveAValidPassengerPayload() {
        payload = loadPayload("add-passenger.json");
    }

    @Given("I have an updated passenger payload")
    public void iHaveAnUpdatedPassengerPayload() {
        payload = loadPayload("edit-passenger.json");
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
        response = RestAssured.given()
                .baseUri(baseUri)
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

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        response = given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
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

    @Then("the passenger with ID {string} should no longer exist")
    public void thePassengerWithIdShouldNoLongerExist(String id) {
        given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .baseUri(baseUri)
                .get(TestUtils.PASSENGER_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private String loadPayload(String filename) {
        try {
            Path filePath = Paths.get("src/integration-test/resources/payload", filename);
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load payload: " + filename, e);
        }
    }
}
