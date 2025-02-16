package com.modsen.driver.it;

import com.modsen.driver.dto.CreateDriverRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void getDriverById_shouldReturnDriver(boolean active) {
            UUID driverId = TestUtils.EXISTING_DRIVER_ID;
            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(driverId.toString()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void getDriverById_shouldReturnNotFound_whenDriverDoesNotExist(boolean active) {
            UUID driverId = TestUtils.NON_EXISTING_DRIVER_ID;

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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void addDriver_shouldReturnCreatedDriver() {
            CreateDriverRequest requestDriver = DriverTestEntityUtils.createDriverRequest();

            String driverId = RestAssuredMockMvc.given()
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

            Driver savedDriver = driverRepository.findById(UUID.fromString(driverId)).orElseThrow();
            assertThat(savedDriver.getFirstName()).isEqualTo(requestDriver.firstName());
            assertThat(savedDriver.getEmail()).isEqualTo(requestDriver.email());
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void addDriver_shouldReturnBadRequest() {
            CreateDriverRequest requestDriver = DriverTestEntityUtils.invalidCreateRequestDriver();

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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void addDriver_shouldReturnConflict_whenEmailExists() {
            CreateDriverRequest requestDriver = DriverTestEntityUtils.createDriverRequest();

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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void editDriver_shouldReturnUpdatedDriver() {
            UUID driverId = TestUtils.EXISTING_DRIVER_ID;
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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void editDriver_shouldReturnNotFound_whenDriverDoesNotExist() {
            UUID driverId = TestUtils.NON_EXISTING_DRIVER_ID;
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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void editDriver_shouldReturnBadRequest() {
            UUID driverId = TestUtils.EDIT_DRIVER_ID;
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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void deleteDriver_shouldReturnNoContent() {
            UUID driverId = TestUtils.EXISTING_DRIVER_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(TestUtils.DRIVER_BY_ID_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void deleteDriver_shouldReturnNotFound_whenDriverDoesNotExist() {
            UUID driverId = TestUtils.NON_EXISTING_DRIVER_ID;

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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void doesDriverExist_shouldReturnOk() {
            UUID driverId = TestUtils.EXISTING_DRIVER_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(TestUtils.DRIVER_EXISTS_URL, driverId.toString())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void doesDriverExist_shouldReturnNotFound_whenDriverDoesNotExistOrIsDeleted() {
            UUID driverId = TestUtils.NON_EXISTING_DRIVER_ID;

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