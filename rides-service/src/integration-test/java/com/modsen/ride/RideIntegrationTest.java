package com.modsen.ride;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.modsen.ride.dto.RequestChangeStatus;
import com.modsen.ride.dto.RequestRide;
import com.modsen.ride.dto.UpdateStatusMessage;
import com.modsen.ride.entity.Ride;
import com.modsen.ride.repo.RideRepository;
import com.modsen.ride.service.KafkaProducer;
import com.modsen.ride.util.ExceptionMessages;
import com.modsen.ride.util.RideStatuses;
import com.modsen.ride.util.RideTestEntityUtils;
import com.modsen.ride.util.TestUtils;
import com.modsen.ride.util.WireMockStubs;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration-test")
public class RideIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RideRepository rideRepository;
    @MockBean
    private KafkaProducer kafkaProducer;
    private static WireMockServer wireMockServer;


    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("ride-test-db")
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
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class GetRide {

        @Test
        public void getRideById_shouldReturnRide() {
            Long rideId = TestUtils.EXIST_ID;

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides/{id}", rideId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(rideId.intValue()))
                    .body("rideStatus", equalTo(RideStatuses.ACCEPTED.toString()));
        }

        @Test
        public void getRideById_shouldReturnNotFound_whenRideDoesNotExist() {
            Long rideId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides/{id}", rideId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.RIDE_NOT_FOUND.format(rideId)));
        }

        @Test
        public void getAllRides_shouldReturnPagedRides() {
            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("rides", notNullValue());
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class AddRide {
        @Test
        public void addRide_shouldReturnCreatedRide() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide().build();

            WireMockStubs.stubPassengerExists(requestRide.passengerId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRide)
                    .when()
                    .post("/api/v1/rides")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue());
        }

        @Test
        public void addRide_shouldReturnPassengerNotFound_whenPassengerDoesNotExistOrIsDeleted() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide().build();
            WireMockStubs.stubPassengerNotExists(requestRide.passengerId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRide)
                    .when()
                    .post("/api/v1/rides")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(TestUtils.PASSENGER_NOT_FOUND_MESSAGE.formatted(requestRide.passengerId())));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class EditRide {
        @Test
        public void editRide_shouldUpdateRide() {
            Long rideId = TestUtils.EDIT_ID;

            RequestRide updateRequestRide = RideTestEntityUtils.createTestRequestRide()
                    .driverId(TestUtils.EDIT_ID)
                    .build();

            WireMockStubs.stubDriverExists(updateRequestRide.driverId());
            WireMockStubs.stubPassengerExists(updateRequestRide.passengerId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(updateRequestRide)
                    .when()
                    .put("/api/v1/rides/{id}", rideId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("driverId", equalTo(updateRequestRide.driverId().intValue()))
                    .body("rideStatus", equalTo(RideStatuses.ACCEPTED.toString()));

            String expectedMessage = TestUtils.UPDATE_RIDE_STATUS_MESSAGE.formatted(rideId, RideStatuses.ACCEPTED);
            Mockito.verify(kafkaProducer, Mockito.times(1))
                    .send(new UpdateStatusMessage(expectedMessage));
        }

        @Test
        public void updateRideStatus_shouldChangeRideStatus() {
            Long rideId = TestUtils.EDIT_ID;

            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.ACCEPTED);

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestChangeStatus)
                    .when()
                    .put("/api/v1/rides/{id}/status", rideId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("rideStatus", equalTo(requestChangeStatus.newStatus().toString()));

            String expectedMessage = TestUtils.UPDATE_RIDE_STATUS_MESSAGE.formatted(rideId, requestChangeStatus.newStatus());
            Mockito.verify(kafkaProducer, Mockito.times(1))
                    .send(new UpdateStatusMessage(expectedMessage));
        }

        @Test
        public void updateRideStatus_shouldReturnConflict_whenInvalidStatusTransaction() {
            Long rideId = TestUtils.EDIT_ID;

            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.PICKING_UP);

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestChangeStatus)
                    .when()
                    .put("/api/v1/rides/{id}/status", rideId.toString())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class DoesRideExist {
        @Test
        public void doesRideExistForDriver_shouldReturnOk() {
            Ride ride = rideRepository.findById(TestUtils.EXIST_ID).orElseThrow();
            Long rideId = ride.getId();
            Long driverId = ride.getDriverId();

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides/{rideId}/driver/{driverId}/exists", rideId.toString(), driverId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        public void doesRideExistForPassenger_shouldReturnOk() {
            Ride ride = rideRepository.findById(TestUtils.EXIST_ID).orElseThrow();
            Long rideId = ride.getId();
            Long passengerId = ride.getPassengerId();

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides/{rideId}/passenger/{passengerId}/exists", rideId.toString(), passengerId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }
}