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

public class RideWireMock {
    private final WireMockServer wireMockServer;

    public RideWireMock(int port) {
        this.wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port));
        this.wireMockServer.start();
        WireMock.configureFor("localhost", port);
    }

    public void stopServer() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    public void stubRideExists(Long rideId, UUID passengerId) {
        String url = TestUtils.RIDE_EXISTS_FOR_PASSENGER_URL
                .replace("{rideId}", rideId.toString())
                .replace("{passengerId}", passengerId.toString());

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));
                        //.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        //.withBody("true")));
    }

    public void stubRideNotExists(Long rideId, UUID passengerId) {
        String url = TestUtils.RIDE_EXISTS_FOR_PASSENGER_URL
                .replace("{rideId}", rideId.toString())
                .replace("{passengerId}", passengerId.toString());

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("Ride with id " + rideId + " not found")));
    }
}
