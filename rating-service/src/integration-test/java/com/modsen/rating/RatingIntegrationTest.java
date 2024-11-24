package com.modsen.rating;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.modsen.rating.dto.RequestRate;
import com.modsen.rating.repo.RateRepository;
import com.modsen.rating.util.ExceptionMessages;
import com.modsen.rating.util.RateTestEntityUtils;
import com.modsen.rating.util.TestUtils;
import com.modsen.rating.util.WireMockStubs;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

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
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class GetRate {

        @Test
        public void getRateById_shouldReturnRate() {
            Long rateId = TestUtils.EXIST_Id;

            RestAssuredMockMvc.given()
                    .when()
                    .get(TestUtils.RATE_BY_ID_URL, rateId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(rateId.intValue()));
        }

        @Test
        public void getRateById_shouldReturnNotFound_whenRateDoesNotExist() {
            Long rateId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .when()
                    .get(TestUtils.RATE_BY_ID_URL, rateId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", equalTo(ExceptionMessages.INSTANCE.rateNotFound(rateId)));
        }

        @Test
        public void getRates_shouldReturnPagedResponse() {
            RestAssuredMockMvc.given()
                    .when()
                    .get(TestUtils.RATE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("list", hasSize(TestUtils.TOTAL_ELEMENTS))
                    .body("totalElements", equalTo(TestUtils.TOTAL_ELEMENTS));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class GetRatesByUser {

        static Stream<Arguments> provideEndpointsAndResults() {
            return Stream.of(
                    Arguments.of("/api/v1/rates/from-passengers", 1, true),
                    Arguments.of("/api/v1/rates/from-drivers", 1, false),
                    Arguments.of("/api/v1/rates/from-passengers/10", 1, true),
                    Arguments.of("/api/v1/rates/from-drivers/11", 1, false)
            );
        }

        @ParameterizedTest
        @MethodSource("provideEndpointsAndResults")
        void getRates_shouldReturnPagedResponse(String endpoint, int expectedSize, boolean isPassenger) {
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
    @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class AddRate {

        @Test
        public void addRate_shouldReturnCreatedRate() {
            RequestRate requestRate = RateTestEntityUtils.INSTANCE.createTestRequestRate();

            WireMockStubs.stubRideExists(requestRate.getRideId(), requestRate.getUserId());
            WireMockStubs.stubPassengerExists(requestRate.getUserId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRate)
                    .when()
                    .post(TestUtils.RATE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue());
        }

        @Test
        public void addRate_shouldReturnRideNotFound_whenRideDoesNotExistForPassenger() {
            RequestRate requestRate = RateTestEntityUtils.INSTANCE.createTestRequestRate();
            String message = TestUtils.RIDE_NOT_FOUND_MESSAGE.formatted(requestRate.getRideId());

            WireMockStubs.stubRideNotExists(requestRate.getRideId(), requestRate.getUserId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRate)
                    .when()
                    .post(TestUtils.RATE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", equalTo(message));
        }
    }
}
