package com.modsen.passenger.it;

import com.modsen.passenger.dto.CreatePassengerRequest;
import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.entity.Passenger;
import com.modsen.passenger.repo.PassengerRepository;
import com.modsen.passenger.util.ExceptionMessages;
import com.modsen.passenger.util.PassengerTestEntityUtils;
import com.modsen.passenger.util.TestUtils;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration-test")
public class PassengerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PassengerRepository passengerRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("passenger-test-db")
            .withUsername("postgres")
            .withPassword("WC4ty37xd3");

    @DynamicPropertySource
    public static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete-exist.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    public class GetPassenger {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void getPassengerById_shouldReturnPassenger(boolean active) {
            UUID passengerId = TestUtils.EXISTING_ID;

            RestAssuredMockMvc.given()
                    .log().all()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.PASSENGER_BY_ID_URL, passengerId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(passengerId.toString()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void getPassengerById_shouldReturnNotFound_whenPassengerDoesNotExist(boolean active) {
            UUID passengerId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.PASSENGER_BY_ID_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void getAllPassengers_shouldReturnPagedPassengers(boolean active) {
            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.PASSENGER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("passengers", notNullValue())
                    .body("totalElements", notNullValue());
        }
    }

    @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Nested
    class AddPassenger {

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void addPassenger_shouldReturnCreatedPassenger() {
            CreatePassengerRequest requestPassenger = PassengerTestEntityUtils.createPassengerRequest();

            String passengerId = RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .post(TestUtils.PASSENGER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue())
                    .body("firstName", equalTo(requestPassenger.firstName()))
                    .body("email", equalTo(requestPassenger.email()))
                    .extract()
                    .path("id");

            Passenger savedPassenger = passengerRepository.findById(UUID.fromString(passengerId)).orElseThrow();
            assertThat(savedPassenger.getFirstName()).isEqualTo(requestPassenger.firstName());
            assertThat(savedPassenger.getEmail()).isEqualTo(requestPassenger.email());
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void addPassenger_shouldReturnBadRequest() {
            CreatePassengerRequest invalidCreateRequestPassenger = PassengerTestEntityUtils.createInvalidCreateRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(invalidCreateRequestPassenger)
                    .when()
                    .post(TestUtils.PASSENGER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Sql(scripts = "classpath:/scripts/setup-edit-passenger.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void addPassenger_shouldReturnConflict_whenEmailExists() {
            CreatePassengerRequest requestPassenger = PassengerTestEntityUtils.createPassengerRequest();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .post(TestUtils.PASSENGER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("email", requestPassenger.email())));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-edit-passenger.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class EditPassenger {

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void editPassenger_shouldReturnUpdatedPassenger() {
            UUID passengerId = TestUtils.EXISTING_ID;
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createUpdateRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .put(TestUtils.PASSENGER_BY_ID_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", notNullValue())
                    .body("firstName", equalTo(requestPassenger.firstName()))
                    .body("email", equalTo(requestPassenger.email()));

            Passenger updatedPassenger = passengerRepository.findById(passengerId).orElseThrow();
            assertThat(updatedPassenger.getFirstName()).isEqualTo(requestPassenger.firstName());
            assertThat(updatedPassenger.getEmail()).isEqualTo(requestPassenger.email());
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void editPassenger_shouldReturnNotFound_whenPassengerDoesNotExist() {
            UUID passengerId = TestUtils.NON_EXISTING_ID;
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createUpdateRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .put(TestUtils.PASSENGER_BY_ID_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void editPassenger_shouldReturnBadRequest() {
            UUID passengerId = TestUtils.EXISTING_ID;
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createInvalidRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .put(TestUtils.PASSENGER_BY_ID_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete-exist.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class DeletePassenger {

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void deletePassenger_shouldReturnNoContent() {
            UUID passengerId = TestUtils.EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(TestUtils.PASSENGER_BY_ID_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void deletePassenger_shouldReturnNotFound_whenPassengerDoesNotExist() {
            UUID passengerId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(TestUtils.PASSENGER_BY_ID_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete-exist.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class PassengerExists {
        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void doesPassengerExist_shouldReturnOk() {
            UUID passengerId = TestUtils.EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(TestUtils.PASSENGER_EXISTS_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        public void doesPassengerExist_shouldReturnNotFound_whenPassengerDoesNotExistOrIsDeleted() {
            UUID passengerId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(TestUtils.PASSENGER_EXISTS_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));
        }
    }
}