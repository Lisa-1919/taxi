package com.modsen.ride.util;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class WireMockStubs {

    public static void stubPassengerExists(Long passengerId) {
        stubFor(WireMock.get(urlPathEqualTo("/api/v1/passengers/" + passengerId + "/exists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("true")));
    }

    public static void stubPassengerNotExists(Long passengerId) {
        stubFor(WireMock.get(urlPathEqualTo("/api/v1/passengers/" + passengerId + "/exists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("Passenger with id " + passengerId + " not found")));
    }

    public static void stubDriverExists(Long driverId) {
        stubFor(WireMock.get(urlPathEqualTo("/api/v1/drivers/" + driverId + "/exists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("true")));
    }
}