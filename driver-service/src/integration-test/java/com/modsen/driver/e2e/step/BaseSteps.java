package com.modsen.driver.e2e.step;

import io.cucumber.java.Before;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseSteps {

    protected static RequestSpecification request;
    private static String accessToken;

    @Before(order = 1)
    public void setup() {
        if (accessToken == null) {
            accessToken = obtainAccessToken();
        }
        request = given().header("Authorization", "Bearer " + accessToken);
    }

    private String obtainAccessToken() {
        Response response = given()
                .contentType("application/json")
                .body("{\"username\": \"test-driver@gmail.com\", \"password\": \"test-password\"}")
                .post("http://localhost:8765/api/v1/account/login");

        return response.jsonPath().getString("access_token");
    }

}
