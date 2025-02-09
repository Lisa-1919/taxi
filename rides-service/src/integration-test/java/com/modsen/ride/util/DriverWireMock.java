package com.modsen.ride.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

public class DriverWireMock {

    private final WireMockServer wireMockServer;

    public DriverWireMock(int port) {
        this.wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port));
        this.wireMockServer.start();
        WireMock.configureFor("localhost", port);
        System.out.println("WireMock Driver Service Running on port " + port);
    }

    public void stopServer() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
            System.out.println("WireMock Driver Service Stopped");
        }
    }

    public void stubDriverExists(UUID driverId) {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching("/api/v1/drivers/.*/exists"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("true")));
    }

}
