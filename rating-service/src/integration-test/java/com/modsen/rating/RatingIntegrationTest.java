package com.modsen.rating;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.modsen.rating.dto.RequestRate;
import com.modsen.rating.entity.Rate;
import com.modsen.rating.repo.RateRepository;
import com.modsen.rating.util.ExceptionMessages;
import com.modsen.rating.util.RateTestEntityUtils;
import com.modsen.rating.util.UserType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration-test")
@Transactional
public class RatingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RateRepository rateRepository;
    private static WireMockServer wireMockServer;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("rating-test-db")
            .withUsername("postgres")
            .withPassword("WC4ty37xd3");

    @DynamicPropertySource
    public static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    static void init() {
        try {
            wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(7070));
            wireMockServer.start();
            WireMock.configureFor("localhost", 7070);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @AfterAll
    static void tearDown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Nested
    class GetRate {

        @Test
        @Transactional
        public void getRateById_shouldReturnRate() {
            Rate rate = RateTestEntityUtils.INSTANCE.createTestRate();
            Rate savedRate = rateRepository.save(rate);
            Long rateId = savedRate.getId();

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rates/" + rateId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(rateId.intValue()))
                    .body("rate", equalTo((float) rate.getRate()));
        }

        @Test
        public void getRideById_shouldReturnNotFound() {
            Long rateId = 999L;

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rates/" + rateId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", equalTo(ExceptionMessages.INSTANCE.rateNotFound(rateId)));
        }

        @Test
        @Transactional
        public void getRates_shouldReturnPagedResponse() {
            Rate rate = RateTestEntityUtils.INSTANCE.createTestRate();
            rateRepository.save(rate);

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rates")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("list", hasSize(1))
                    .body("totalElements", equalTo(1));
        }
    }

    @Nested
    class GetRatesByUser {

        static Stream<Arguments> provideEndpointsAndResults() {
            return Stream.of(
                    Arguments.of("/api/v1/rates/from-passengers", 1, true),
                    Arguments.of("/api/v1/rates/from-drivers", 1, false),
                    Arguments.of("/api/v1/rates/from-passengers/1", 1, true),
                    Arguments.of("/api/v1/rates/from-drivers/1", 1, false)
            );
        }

        @ParameterizedTest
        @MethodSource("provideEndpointsAndResults")
        @Transactional
        void getRates_shouldReturnPagedResponse(String endpoint, int expectedSize, boolean isPassenger) {
            Rate rate = RateTestEntityUtils.INSTANCE.createTestRate();

            if(!isPassenger) rate.setUserType(UserType.DRIVER);

            rateRepository.save(rate);

            RestAssuredMockMvc.given()
                    .when()
                    .get(endpoint)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("list", hasSize(expectedSize))
                    .body("totalElements", equalTo(expectedSize));
        }
    }

    @Nested
    class AddRate {

        @Test
        @Transactional
        public void addRate_shouldReturnCreatedRate() {
            RequestRate requestRate = RateTestEntityUtils.INSTANCE.createTestRequestRate();

            stubFor(WireMock.get(urlMatching("/api/v1/rides/" + requestRate.getRideId() + "/passenger/" + requestRate.getUserId() + "/exists"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("true")));

            stubFor(WireMock.get(urlMatching("/api/v1/passengers/" + requestRate.getUserId() + "/exists"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("true")));

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRate)
                    .when()
                    .post("/api/v1/rates")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue());
        }

        @Test
        @Transactional
        public void addRate_shouldReturnRideNotFound() {
            RequestRate requestRate = RateTestEntityUtils.INSTANCE.createTestRequestRate();
            String message = "Ride with id " + requestRate.getRideId() + "not found";

            stubFor(WireMock.get(urlMatching("/api/v1/rides/" + requestRate.getRideId() + "/passenger/" + requestRate.getUserId() + "/exists"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.NOT_FOUND.value())
                            .withBody(message)));

            stubFor(WireMock.get(urlMatching("/api/v1/passengers/" + requestRate.getUserId() + "/exists"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withBody("true")));

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRate)
                    .when()
                    .post("/api/v1/rates")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", equalTo(message));
        }
    }
}
