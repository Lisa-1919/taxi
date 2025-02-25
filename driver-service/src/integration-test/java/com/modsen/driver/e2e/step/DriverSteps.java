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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DriverSteps {

    private Response response;
    private final String baseUri = "http://localhost:8765/";
    private String payload;
    private String accessToken;

    @Given("I authenticate as {string} with password {string}")
    public void iAuthenticateAs(String username, String password) {
        response = given()
                .contentType("application/json")
                .body("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
                .post(baseUri + "api/v1/account/login");

        response.then().statusCode(200);
        accessToken = response.jsonPath().getString("access_token");
        System.out.println(accessToken);
        assertNotNull(accessToken, "Access token must not be null");
    }

    @Given("the driver with ID {string} exists")
    public void theDriverWithIdExists(String id) {
        given()
                .baseUri(baseUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .get(TestUtils.DRIVER_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @When("I send a GET request for a driver to {string}")
    public void iSendAGetRequestForDriverTo(String endpoint) {
        response = given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
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
        response = given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
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

    @Then("the driver with ID {string} should no longer exist")
    public void theDriverWithIdShouldNoLongerExist(String id) {
        given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .baseUri(baseUri)
                .get(TestUtils.DRIVER_BY_ID_URL, id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @When("I send a PUT request for a driver to {string} with the payload")
    public void iSendAPutRequestForDriverToWithThePayload(String endpoint) {
        response = given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
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
