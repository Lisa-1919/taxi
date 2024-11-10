package com.modsen.driver;

import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.entity.Driver;
import com.modsen.driver.repo.DriverRepository;
import com.modsen.driver.util.ExceptionMessages;
import com.modsen.driver.utils.DriverTestEntityUtils;
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
public class DriverIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverRepository driverRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("driver-test-db")
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
    class GetDriver {

        @Autowired
        private DriverRepository driverRepository;

        private Driver nonDeletedDriver;

        @BeforeAll
        private void setUp() {
            nonDeletedDriver = DriverTestEntityUtils.createTestDriver();
            nonDeletedDriver.setId(null);
            driverRepository.save(nonDeletedDriver);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getDriverById_shouldReturnDriver(boolean active) {
            RestAssuredMockMvc.given()
                    .param("active", active)
                    .when()
                    .get("/api/v1/drivers/" + nonDeletedDriver.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(nonDeletedDriver.getId().intValue()))
                    .body("firstName", equalTo(nonDeletedDriver.getFirstName()))
                    .body("email", equalTo(nonDeletedDriver.getEmail()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getDriverByIdNonDeleted_shouldReturnNotFound(boolean active) {
            Long driverId = 999L;

            RestAssuredMockMvc.given()
                    .param("active", active)
                    .when()
                    .get("/api/v1/drivers/" + driverId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getAllDrivers_shouldReturnPagedDrivers(boolean active) {
            RestAssuredMockMvc.given()
                    .param("active", active)
                    .param("page", 0)
                    .param("size", 10)
                    .when()
                    .get("/api/v1/drivers")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("drivers", hasSize(1))
                    .body("totalElements", equalTo(1));
        }

    }

    @Nested
    class AddDriver {
        @Transactional
        @Test
        public void addDriver_shouldReturnCreatedDriver() {
            RequestDriver requestDriver = DriverTestEntityUtils.createTestRequestDriver();

            Integer driverId = RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .post("/api/v1/drivers")
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

        //check exception message
        @Transactional
        @Test
        public void addDriver_shouldReturnBadRequest() {
            RequestDriver requestDriver = DriverTestEntityUtils.createInvalidRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .post("/api/v1/drivers")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Transactional
        @Test
        public void addDriver_shouldReturnConflictWhenEmailExists() {
            RequestDriver requestDriver1 = DriverTestEntityUtils.createTestRequestDriver();
            RequestDriver requestDriver2 = DriverTestEntityUtils.createTestRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver1)
                    .when()
                    .post("/api/v1/drivers")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver2)
                    .when()
                    .post("/api/v1/drivers")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", requestDriver2.email())));
        }
    }

    @Nested
    class EditDriver {

        @Transactional
        @Test
        public void editDriver_shouldReturnUpdatedDriver() {
            Driver driver = DriverTestEntityUtils.createTestDriver();
            driver.setId(null);
            driverRepository.save(driver);
            Long driverId = driver.getId();
            RequestDriver requestDriver = DriverTestEntityUtils.createUpdateRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .put("/api/v1/drivers/" + driverId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", notNullValue())
                    .body("firstName", equalTo(requestDriver.firstName()));

            Driver updatedDriver = driverRepository.findById(driverId).orElseThrow();
            assertThat(updatedDriver.getFirstName()).isEqualTo(requestDriver.firstName());
            assertThat(updatedDriver.getEmail()).isEqualTo(requestDriver.email());
        }

        @Transactional
        @Test
        public void editDriver_shouldReturnNotFound() {
            Long driverId = 999L;
            RequestDriver requestDriver = DriverTestEntityUtils.createUpdateRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .put("/api/v1/drivers/" + driverId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }

        @Transactional
        @Test
        public void editDriver_shouldReturnBadRequest() {
            Driver driver = DriverTestEntityUtils.createTestDriver();
            driver.setId(null);
            driverRepository.save(driver);
            Long driverId = driver.getId();
            RequestDriver requestDriver = DriverTestEntityUtils.createInvalidRequestDriver();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDriver)
                    .when()
                    .put("/api/v1/drivers/" + driverId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class DeleteDriver {

        @Transactional
        @Test
        public void deleteDriver_shouldReturnNoContent() {
            Driver driver = DriverTestEntityUtils.createTestDriver();
            driver.setId(null);
            driverRepository.save(driver);
            Long driverId = driver.getId();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/api/v1/drivers/" + driverId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Transactional
        @Test
        public void deleteDriver_shouldReturnNotFound() {
            Long driverId = 999L;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/api/v1/drivers/" + driverId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }
    }

    @Nested
    class DriverExists {

        @Transactional
        @Test
        public void doesDriverExist_shouldReturnOk() {
            Driver driver = DriverTestEntityUtils.createTestDriver();
            driver.setId(null);
            driverRepository.save(driver);
            Long driverId = driver.getId();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/v1/drivers/" + driverId + "/exists")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        public void doesDriverExist_shouldReturnNotFound() {
            Long driverId = 999L;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/v1/drivers/" + driverId + "/exists", String.valueOf(driverId))
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }
    }
}