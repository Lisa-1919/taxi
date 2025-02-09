package com.modsen.ride.e2e.step;

import com.modsen.ride.util.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.client.methods.HttpHead;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class RideSteps {

    private Response response;
    private final String baseUri = "http://localhost:8083";
    private String payload;
    public String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJWZUdSdWRITjNrSjNQTXoweXczQVM2ZzZTVnlVVzlxb3FmRGctak15bVQwIn0.eyJleHAiOjE3Mzk1NTA2MjgsImlhdCI6MTczOTU0ODgyOSwianRpIjoiZGU5ZjM1YmUtYThhOC00OGVkLThkOWYtYjNhYjYyZTQxMDI2IiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3RheGkiLCJhdWQiOlsiYXV0aC1zZXJ2aWNlIiwiYWNjb3VudCJdLCJzdWIiOiI2YjUwZDUwYS04NDkyLTRlZjEtOTAyZi1lMWEyNjJiN2MzN2MiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhdXRoIiwic2lkIjoiMTE5OTczY2YtMjJiMS00Mzk2LTlmZWUtYzA5NjFkN2UyYTUzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiUk9MRV9vZmZsaW5lX2FjY2VzcyIsIlJPTEVfUEFTU0VOR0VSIiwiUk9MRV9kZWZhdWx0LXJvbGVzLXRheGkiLCJST0xFX3VtYV9hdXRob3JpemF0aW9uIiwiUk9MRV9EUklWRVIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJ0ZXN0IHRlc3QiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJ0ZXN0IiwiZmFtaWx5X25hbWUiOiJ0ZXN0IiwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSJ9.Be0xS77mErtKaICkQrQsqEBqgtLTPvAxizq23jL3tI4-hp4z3YbTnCxV9j8qOokf5i_q0z_26YwY6UoIEiN6zHLWexk9wI5QBPNu32C54d4YobcYUxZUtZOOzZ0_l5QXUq6qk2yjk7PK0s-tEw-WtVzvovf2SUF4UFMqGliMQdgLnGX_Na6sPB9QeX0nhhJexMthWOTtC73JtX-7uyQvcFaAaNgvfVDXe7d36kjK5MtEyskVfbOcJMawxicnNRhb1-ZIlODcOq5_2vr4JbHTOHuMU2KolBMqyn_LeSRKkikAO8bw-rh4MGHzhL0BxFsDkJzemR22qa5R2R2uputopw";

    @Given("the ride with ID {int} exists")
    public void theRideWithIdExists(int id) {
        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, token)
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
        response = RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, token)
                .baseUri(baseUri)
                .get(endpoint);
    }

    @When("I send a POST request to {string} with the payload")
    public void iSendAPostRequestToWithThePayload(String endpoint) {
        response = RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, token)
                .baseUri(baseUri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .post(endpoint);
    }

    @When("I send a PUT request to {string} with the payload")
    public void iSendAPutRequestToWithThePayload(String endpoint) {
        response = RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, token)
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
