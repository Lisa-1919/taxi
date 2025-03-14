package com.modsen.ride.it;

import com.modsen.ride.dto.RequestChangeStatus;
import com.modsen.ride.dto.RequestRide;
import com.modsen.ride.dto.UpdateStatusMessage;
import com.modsen.ride.entity.Ride;
import com.modsen.ride.repo.RideRepository;
import com.modsen.ride.service.KafkaProducer;
import com.modsen.ride.util.DriverWireMock;
import com.modsen.ride.util.ExceptionMessages;
import com.modsen.ride.util.PassengerWireMock;
import com.modsen.ride.util.RideStatuses;
import com.modsen.ride.util.RideTestEntityUtils;
import com.modsen.ride.util.TestUtils;
import com.redis.testcontainers.RedisContainer;
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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
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

    private static DriverWireMock driverWireMock;
    private static PassengerWireMock passengerWireMock;

    @BeforeAll
    static void init() {
        try {
            driverWireMock = new DriverWireMock(8081);
            passengerWireMock = new PassengerWireMock(8082);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @AfterAll
    static void tearDown(@Autowired RedisConnectionFactory redisConnectionFactory) {
        driverWireMock.stopServer();
        passengerWireMock.stopServer();

        if (redisConnectionFactory instanceof LettuceConnectionFactory) {
            ((LettuceConnectionFactory) redisConnectionFactory).destroy();
        }
    }

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("ride-test-db")
            .withUsername("postgres")
            .withPassword("WC4ty37xd3");

    @Container
    public static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:6.2.6"));

    @DynamicPropertySource
    public static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getRedisPort);
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
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void getRideById_shouldReturnRide() {
            Long rideId = TestUtils.EXIST_ID;

            RestAssuredMockMvc.given()
                    .when()
                    .get(TestUtils.RIDE_BY_ID_URL, rideId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(rideId.intValue()))
                    .body("rideStatus", equalTo(RideStatuses.ACCEPTED.toString()));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void getRideById_shouldReturnNotFound_whenRideDoesNotExist() {
            Long rideId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .when()
                    .get(TestUtils.RIDE_BY_ID_URL, rideId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("message", equalTo(ExceptionMessages.RIDE_NOT_FOUND.format(rideId)));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void getAllRides_shouldReturnPagedRides() {
            RestAssuredMockMvc.given()
                    .when()
                    .get(TestUtils.RIDE_BASE_URL)
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
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void addRide_shouldReturnCreatedRide() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide().build();

            passengerWireMock.stubPassengerExists(requestRide.passengerId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRide)
                    .when()
                    .post(TestUtils.RIDE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue());
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void addRide_shouldReturnPassengerNotFound_whenPassengerDoesNotExistOrIsDeleted() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide().driverId(null).build();

            passengerWireMock.stubPassengerNotExists(requestRide.passengerId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestRide)
                    .when()
                    .post(TestUtils.RIDE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("message", equalTo(TestUtils.PASSENGER_NOT_FOUND_MESSAGE.formatted(requestRide.passengerId())));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class EditRide {
        @Test
        @WithMockUser(roles = { "DRIVER" }, username = "driver@gmail.com")
        public void editRide_shouldUpdateRide() {
            Long rideId = TestUtils.EDIT_ID;

            RequestRide updateRequestRide = RideTestEntityUtils.createTestRequestRide()
                    .driverId(TestUtils.EDIT_DRIVER_ID)
                    .build();

            driverWireMock.stubDriverExists(updateRequestRide.driverId());
            passengerWireMock.stubPassengerExists(updateRequestRide.passengerId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateRequestRide)
                    .when()
                    .put(TestUtils.RIDE_BY_ID_URL, rideId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("driverId", equalTo(updateRequestRide.driverId().toString()))
                    .body("rideStatus", equalTo(RideStatuses.ACCEPTED.toString()));

            String expectedMessage = TestUtils.UPDATE_RIDE_STATUS_MESSAGE.formatted(rideId, RideStatuses.ACCEPTED);
            Mockito.verify(kafkaProducer, Mockito.times(1))
                    .send(new UpdateStatusMessage(expectedMessage));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void updateRideStatus_shouldChangeRideStatus() {
            Long rideId = TestUtils.EDIT_ID;

            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.ACCEPTED);

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestChangeStatus)
                    .when()
                    .put(TestUtils.RIDE_STATUS_URL, rideId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("rideStatus", equalTo(requestChangeStatus.newStatus().toString()));

            String expectedMessage = TestUtils.UPDATE_RIDE_STATUS_MESSAGE.formatted(rideId, requestChangeStatus.newStatus());
            Mockito.verify(kafkaProducer, Mockito.times(1))
                    .send(new UpdateStatusMessage(expectedMessage));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void updateRideStatus_shouldReturnConflict_whenInvalidStatusTransaction() {
            Long rideId = TestUtils.EDIT_ID;

            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.PICKING_UP);

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestChangeStatus)
                    .when()
                    .put(TestUtils.RIDE_STATUS_URL, rideId.toString())
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
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void doesRideExistForDriver_shouldReturnOk() {
            Ride ride = rideRepository.findById(TestUtils.EXIST_ID).orElseThrow();
            Long rideId = ride.getId();
            UUID driverId = ride.getDriverId();

            RestAssuredMockMvc.given()
                    .when()
                    .get(TestUtils.RIDE_EXISTS_FOR_DRIVER_URL, rideId.toString(), driverId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void doesRideExistForPassenger_shouldReturnOk() {
            Ride ride = rideRepository.findById(TestUtils.EXIST_ID).orElseThrow();
            Long rideId = ride.getId();
            UUID passengerId = ride.getPassengerId();

            RestAssuredMockMvc.given()
                    .when()
                    .get(TestUtils.RIDE_EXISTS_FOR_PASSENGER_URL, rideId.toString(), passengerId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }
}