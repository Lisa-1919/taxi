package com.modsen.driver.it;

import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.entity.Driver;
import com.modsen.driver.repo.DriverRepository;
import com.modsen.driver.util.ExceptionMessages;
import com.modsen.driver.util.TestUtils;
import com.modsen.driver.utils.DriverTestEntityUtils;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
public class DriverIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverRepository driverRepository;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
    })
    class GetDriver {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getDriverById_shouldReturnDriver(boolean active) {
            Long driverId = TestUtils.EXISTING_ID;
            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(driverId.intValue()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getDriverById_shouldReturnNotFound_whenDriverDoesNotExist(boolean active) {
            Long driverId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getAllDrivers_shouldReturnPagedDrivers(boolean active) {
            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.DRIVER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("drivers", notNullValue())
                    .body("totalElements", notNullValue());
        }

    }

    @Nested
    @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class AddDriver {
        @Test
        public void addDriver_shouldReturnCreatedDriver() {
            RequestDriver requestDriver = DriverTestEntityUtils.createTestRequestDriver();

            Integer driverId = RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .post(TestUtils.DRIVER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue())
                    .body("firstName", equalTo(requestDriver.firstName()))
                    .body("email", equalTo(requestDriver.email()))
                    .extract()
                    .path("id");

            Driver savedDriver = driverRepository.findById(Long.valueOf(driverId)).orElseThrow();
            assertThat(savedDriver.getFirstName()).isEqualTo(requestDriver.firstName());
            assertThat(savedDriver.getEmail()).isEqualTo(requestDriver.email());
        }

        @Test
        public void addDriver_shouldReturnBadRequest() {
            RequestDriver requestDriver = DriverTestEntityUtils.createInvalidRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .post(TestUtils.DRIVER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @SqlGroup({
                @Sql(scripts = "classpath:/scripts/setup-add-edit.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        })
        @Test
        public void addDriver_shouldReturnConflict_whenEmailExists() {
            RequestDriver requestDriver = DriverTestEntityUtils.createTestRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .post(TestUtils.DRIVER_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", requestDriver.email())));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
    })
    class EditDriver {

        @Test
        public void editDriver_shouldReturnUpdatedDriver() {
            Long driverId = TestUtils.EXISTING_ID;
            RequestDriver requestDriver = DriverTestEntityUtils.createUpdateRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .put(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", notNullValue())
                    .body("firstName", equalTo(requestDriver.firstName()));

            Driver updatedDriver = driverRepository.findById(driverId).orElseThrow();
            assertThat(updatedDriver.getFirstName()).isEqualTo(requestDriver.firstName());
            assertThat(updatedDriver.getEmail()).isEqualTo(requestDriver.email());
        }

        @Test
        public void editDriver_shouldReturnNotFound_whenDriverDoesNotExist() {
            Long driverId = TestUtils.NON_EXISTING_ID;
            RequestDriver requestDriver = DriverTestEntityUtils.createUpdateRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .put(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }

        @Test
        public void editDriver_shouldReturnBadRequest() {
            Long driverId = TestUtils.EDIT_ID;
            RequestDriver requestDriver = DriverTestEntityUtils.createInvalidRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .put(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
    })
    class DeleteDriver {

        @Test
        public void deleteDriver_shouldReturnNoContent() {
            Long driverId = TestUtils.EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        public void deleteDriver_shouldReturnNotFound_whenDriverDoesNotExist() {
            Long driverId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
    })
    class DriverExists {

        @Test
        public void doesDriverExist_shouldReturnOk() {
            Long driverId = TestUtils.EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(TestUtils.DRIVER_EXISTS_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        public void doesDriverExist_shouldReturnNotFound_whenDriverDoesNotExistOrIsDeleted() {
            Long driverId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(TestUtils.DRIVER_EXISTS_URL, String.valueOf(driverId))
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }
    }
}