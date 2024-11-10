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
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
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
    class GetRide {

        @Test
        @Transactional
        public void getRideById_shouldReturnRide() {
            Ride ride = RideTestEntityUtils.createTestRide()
                    .id(null)
                    .build();
            rideRepository.save(ride);
            Long rideId = ride.getId();

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides/" + rideId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(rideId.intValue()))
                    .body("rideStatus", equalTo(RideStatuses.CREATED.toString()));
        }

        @Test
        public void getRideById_shouldReturnNotFound() {
            Long rideId = 999L;

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides/" + rideId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.RIDE_NOT_FOUND.format(rideId)));
        }

        @Test
        public void getAllRides_shouldReturnPagedRides() {
            Ride ride = RideTestEntityUtils.createTestRide()
                    .id(null)
                    .build();
            rideRepository.save(ride);

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("rides", hasSize(1))
                    .body("totalElements", equalTo(1));
        }
    }

    @Nested
    class AddRide {
        @Test
        @Transactional
        public void addRide_shouldReturnCreatedRide() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide().build();

            stubFor(WireMock.get(urlMatching("/api/v1/passengers/" + requestRide.passengerId() + "/exists"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("true")));

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
        public void addRide_shouldReturnPassengerNotFound() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide().build();
            String message = "Passenger with id " + requestRide.passengerId() + "not found";

            stubFor(WireMock.get(urlMatching("/api/v1/passengers/" + requestRide.passengerId() + "/exists"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.NOT_FOUND.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody(message)));

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRide)
                    .when()
                    .post("/api/v1/rides")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(message));
        }
    }

    @Nested
    class EditRide {
        @Test
        @Transactional
        public void editRide_shouldUpdateRide() {

            Ride ride = RideTestEntityUtils.createTestRide()
                    .id(null)
                    .driverId(null)
                    .rideStatus(RideStatuses.CREATED)
                    .build();
            rideRepository.save(ride);
            Long rideId = ride.getId();

            RequestRide updateRequestRide = RideTestEntityUtils.createTestRequestRide()
                    .driverId(1L)
                    .build();

            stubFor(get(urlPathEqualTo("/api/v1/drivers/" + updateRequestRide.driverId() + "/exists"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("true")));
            stubFor(get(urlPathEqualTo("/api/v1/passengers/" + ride.getPassengerId() + "/exists"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("true")));

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(updateRequestRide)
                    .when()
                    .put("/api/v1/rides/" + rideId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("driverId", equalTo(updateRequestRide.driverId().intValue()))
                    .body("rideStatus", equalTo(RideStatuses.ACCEPTED.toString()));

            String expectedMessage = String.format("The status of your ride with id %d changed to %s", rideId, RideStatuses.ACCEPTED);
            Mockito.verify(kafkaProducer, Mockito.times(1))
                    .send(new UpdateStatusMessage(expectedMessage));
        }

        @Test
        @Transactional
        public void updateRideStatus_shouldChangeRideStatus() {
            Ride ride = RideTestEntityUtils.createTestRide()
                    .driverId(1L)
                    .rideStatus(RideStatuses.ACCEPTED)
                    .build();
            rideRepository.save(ride);
            Long rideId = ride.getId();

            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.PICKING_UP);

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestChangeStatus)
                    .when()
                    .put("/api/v1/rides/" + rideId + "/status")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("rideStatus", equalTo(requestChangeStatus.newStatus().toString()));

            String expectedMessage = String.format("The status of your ride with id %d changed to %s", rideId, requestChangeStatus.newStatus());
            Mockito.verify(kafkaProducer, Mockito.times(1))
                    .send(new UpdateStatusMessage(expectedMessage));
        }
    }

    @Nested
    class DoesRideExist {
        @Test
        @Transactional
        public void doesRideExistForDriver_shouldReturnOk() {
            Ride ride = RideTestEntityUtils.createTestRide()
                    .id(null)
                    .driverId(1L)
                    .rideStatus(RideStatuses.ACCEPTED)
                    .build();
            rideRepository.save(ride);

            Long rideId = ride.getId();
            Long driverId = ride.getDriverId();

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides/" + rideId + "/driver/" + driverId + "/exists")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @Transactional
        public void doesRideExistForPassenger_shouldReturnOk() {
            Ride ride = RideTestEntityUtils.createTestRide()
                    .id(null)
                    .build();
            rideRepository.save(ride);
            Long rideId = ride.getId();
            Long passengerId = ride.getPassengerId();

            RestAssuredMockMvc.given()
                    .when()
                    .get("/api/v1/rides/" + rideId + "/passenger/" + passengerId + "/exists")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }
}