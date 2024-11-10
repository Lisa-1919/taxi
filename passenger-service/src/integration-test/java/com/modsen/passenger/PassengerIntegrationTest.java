package com.modsen.passenger;

import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.entity.Passenger;
import com.modsen.passenger.repo.PassengerRepository;
import com.modsen.passenger.util.ExceptionMessages;
import com.modsen.passenger.util.PassengerTestEntityUtils;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
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


    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    public class GetPassenger {

        @Autowired
        private PassengerRepository passengerRepository;

        private Passenger nonDeletedPassenger;

        @BeforeAll
        private void setUp() {
            nonDeletedPassenger = PassengerTestEntityUtils.createTestPassenger();
            nonDeletedPassenger.setId(null);
            passengerRepository.save(nonDeletedPassenger);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getPassengerById_shouldReturnPassenger(boolean active) {
            RestAssuredMockMvc.given()
                    .log().all()
                    .param("active", active)
                    .when()
                    .get("/api/v1/passengers/" + nonDeletedPassenger.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(nonDeletedPassenger.getId().intValue()))
                    .body("firstName", equalTo(nonDeletedPassenger.getFirstName()))
                    .body("email", equalTo(nonDeletedPassenger.getEmail()));
        }
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getPassengerById_shouldReturnNotFound(boolean active) {
            Long passengerId = 999L;

            RestAssuredMockMvc.given()
                    .param("active", active)
                    .when()
                    .get("/api/v1/passengers/" + passengerId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getAllNonDeletedPassengers_shouldReturnPagedPassengers(boolean active) {
            RestAssuredMockMvc.given()
                    .param("active", active)
                    .param("page", 0)
                    .param("limit", 10)
                    .when()
                    .get("/api/v1/passengers")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("passengers", hasSize(1))
                    .body("totalElements", equalTo(1));
        }
    }

    @Nested
    class AddPassenger {

        @Transactional
        @Test
        public void addPassenger_shouldReturnCreatedPassenger() {
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createTestRequestPassenger();

            Integer passengerId = RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .post("/api/v1/passengers")
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
                    .post("/api/v1/passengers")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Transactional
        @Test
        public void addPassenger_shouldReturnConflictWhenEmailExists() {
            RequestPassenger requestPassenger1 = PassengerTestEntityUtils.createTestRequestPassenger();
            RequestPassenger requestPassenger2 = PassengerTestEntityUtils.createTestRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger1)
                    .when()
                    .post("/api/v1/passengers")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger2)
                    .when()
                    .post("/api/v1/passengers")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("email", requestPassenger2.email())));
        }
    }

    @Nested
    class EditPassenger {

        @Transactional
        @Test
        public void editPassenger_shouldReturnUpdatedPassenger() {
            Passenger passenger = PassengerTestEntityUtils.createTestPassenger();
            passenger.setId(null);
            passengerRepository.save(passenger);
            Long passengerId = passenger.getId();
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createUpdateRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .put("/api/v1/passengers/" + passengerId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", notNullValue())
                    .body("firstName", equalTo(requestPassenger.firstName()))
                    .body("email", equalTo(requestPassenger.email()));

            Passenger updatedPassenger = passengerRepository.findById(passengerId).orElseThrow();
            assertThat(updatedPassenger.getFirstName()).isEqualTo(requestPassenger.firstName());
            assertThat(updatedPassenger.getEmail()).isEqualTo(requestPassenger.email());
        }

        @Transactional
        @Test
        public void editPassenger_shouldReturnNotFound() {
            Long passengerId = 999L;
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createUpdateRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .put("/api/v1/passengers/" + passengerId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));
        }

        @Transactional
        @Test
        public void editPassenger_shouldReturnBadRequest() {
            Long passengerId = 1L;
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createInvalidRequestPassenger();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestPassenger)
                    .when()
                    .put("/api/v1/passengers/" + passengerId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class DeletePassenger {

        @Transactional
        @Test
        public void deletePassenger_shouldReturnNoContent() {
            Passenger passenger = PassengerTestEntityUtils.createTestPassenger();
            passenger.setId(null);
            passengerRepository.save(passenger);
            Long passengerId = passenger.getId();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/api/v1/passengers/" + passengerId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Transactional
        @Test
        public void deletePassenger_shouldReturnNotFound() {
            Long passengerId = 999L;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/api/v1/passengers/" + passengerId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));
        }
    }

    @Nested
    class PassengerExists {
        @Transactional
        @Test
        public void doesPassengerExist_shouldReturnOk() {
            Passenger passenger = PassengerTestEntityUtils.createTestPassenger();
            passenger.setId(null);
            passengerRepository.save(passenger);
            Long passengerId = passenger.getId();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/v1/passengers/" + passengerId + "/exists")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Transactional
        @Test
        public void doesPassengerExist_shouldReturnNotFound() {
            Long passengerId = 999L;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/v1/passengers/" + passengerId + "/exists")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));
        }
    }

}