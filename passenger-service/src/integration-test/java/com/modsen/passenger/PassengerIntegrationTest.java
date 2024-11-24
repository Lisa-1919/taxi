package com.modsen.passenger;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration-test")
@Transactional
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
    public static void configureTestDatabase(DynamicPropertyRegistry registry) {
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
        public void getPassengerById_shouldReturnPassenger(boolean active) {
            Long passengerId = TestUtils.EXISTING_ID;

            RestAssuredMockMvc.given()
                    .log().all()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.PASSENGER_BY_ID_URL, passengerId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(passengerId.intValue()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getPassengerById_shouldReturnNotFound_whenPassengerDoesNotExist(boolean active) {
            Long passengerId = TestUtils.NON_EXISTING_ID;

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
        public void addPassenger_shouldReturnCreatedPassenger() {
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createTestRequestPassenger();

            Integer passengerId = RestAssuredMockMvc.given()
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

            Passenger savedPassenger = passengerRepository.findById(Long.valueOf(passengerId)).orElseThrow();
            assertThat(savedPassenger.getFirstName()).isEqualTo(requestPassenger.firstName());
            assertThat(savedPassenger.getEmail()).isEqualTo(requestPassenger.email());
        }

        @Test
        public void addPassenger_shouldReturnBadRequest() {
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createInvalidRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .post(TestUtils.PASSENGER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Sql(scripts = "classpath:/scripts/setup-edit-passenger.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Test
        public void addPassenger_shouldReturnConflict_whenEmailExists() {
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createTestRequestPassenger();

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
        public void editPassenger_shouldReturnUpdatedPassenger() {
            Long passengerId = TestUtils.EDIT_ID;
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
        public void editPassenger_shouldReturnNotFound_whenPassengerDoesNotExist() {
            Long passengerId = TestUtils.NON_EXISTING_ID;
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
        public void editPassenger_shouldReturnBadRequest() {
            Long passengerId = TestUtils.EXISTING_ID;
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
        public void deletePassenger_shouldReturnNoContent() {
            Long passengerId = TestUtils.EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(TestUtils.PASSENGER_BY_ID_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        public void deletePassenger_shouldReturnNotFound_whenPassengerDoesNotExist() {
            Long passengerId = TestUtils.NON_EXISTING_ID;

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
        public void doesPassengerExist_shouldReturnOk() {
            Long passengerId = TestUtils.EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(TestUtils.PASSENGER_EXISTS_URL, String.valueOf(passengerId))
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        public void doesPassengerExist_shouldReturnNotFound_whenPassengerDoesNotExistOrIsDeleted() {
            Long passengerId = TestUtils.NON_EXISTING_ID;

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