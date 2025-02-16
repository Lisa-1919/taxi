package com.modsen.rating.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class PassengerWireMock {

    private final WireMockServer wireMockServer;

    public PassengerWireMock(int port) {
        this.wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port));
        this.wireMockServer.start();
        WireMock.configureFor("localhost", port);
    }

    public void stopServer() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    public void stubPassengerExists(UUID passengerId) {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(TestUtils.PASSENGER_EXISTS_URL.replace("{id}", passengerId.toString())))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("true")));
    }

    public void stubPassengerNotExists(UUID passengerId) {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(TestUtils.PASSENGER_EXISTS_URL.replace("{id}", passengerId.toString())))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("Passenger with id " + passengerId + " not found")));
    }
}
