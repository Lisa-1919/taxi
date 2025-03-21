package com.modsen.driver.it;

import com.modsen.driver.dto.RequestCar;
import com.modsen.driver.entity.Car;
import com.modsen.driver.repo.CarRepository;
import com.modsen.driver.util.ExceptionMessages;
import com.modsen.driver.util.TestUtils;
import com.modsen.driver.utils.CarTestEntityUtils;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
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
public class CarIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
    })
    class GetCar {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void getCarById_shouldReturnCar(boolean active) {
            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.CAR_BY_ID_URL, String.valueOf(TestUtils.EXISTING_ID))
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void getCarById_shouldReturnNotFound_whenCarDoesNotExist(boolean active) {
            Long carId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.CAR_BY_ID_URL, carId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("message", equalTo(ExceptionMessages.CAR_NOT_FOUND.format(carId)));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void getAllCars_shouldReturnPagedCars(boolean active) {
            RestAssuredMockMvc.given()
                    .param(TestUtils.ACTIVE_PARAM, active)
                    .when()
                    .get(TestUtils.CAR_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("cars", notNullValue())
                    .body("totalElements", notNullValue());
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-add-edit.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class AddCar {

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addCar_shouldReturnCreatedCar() throws Exception {
            RequestCar requestCar = CarTestEntityUtils.createTestRequestCar(TestUtils.EDIT_DRIVER_ID);

            Integer carId = RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar)
                    .when()
                    .post(TestUtils.CAR_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("licensePlate", equalTo(requestCar.licensePlate()))
                    .body("driverId", equalTo(TestUtils.EDIT_DRIVER_ID.toString()))
                    .extract()
                    .path("id");

            Car savedCar = carRepository.findById(Long.valueOf(carId)).orElseThrow();
            assertThat(savedCar.getLicensePlate()).isEqualTo(requestCar.licensePlate());
            assertThat(savedCar.getDriver().getId()).isEqualTo(TestUtils.EDIT_DRIVER_ID);
        }

        @Test
        @SqlGroup({
                @Sql(scripts = "classpath:/scripts/setup-duplicate-license-plate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        })
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void addCar_shouldReturnBadRequest_whenLicensePlateIsDuplicate() throws Exception {
            RequestCar requestCar = CarTestEntityUtils.createTestRequestCar(TestUtils.EDIT_DRIVER_ID);
            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar)
                    .when()
                    .post(TestUtils.CAR_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("message", equalTo(ExceptionMessages.DUPLICATE_CAR_ERROR.format("licensePlate", requestCar.licensePlate())));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void addCar_shouldReturnNotFound_whenDriverNotFound() throws Exception {
            UUID driverId = TestUtils.NON_EXISTING_DRIVER_ID;
            RequestCar requestCar = CarTestEntityUtils.createTestRequestCar(driverId);
            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar)
                    .when()
                    .post(TestUtils.CAR_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("message", equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-add-edit.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class EditCar {

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editCar_shouldReturnUpdatedCar() throws Exception {
            RequestCar requestCar = CarTestEntityUtils.createTestRequestCar(TestUtils.EXISTING_DRIVER_ID);

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar)
                    .when()
                    .put(TestUtils.CAR_BY_ID_URL, String.valueOf(TestUtils.EXISTING_ID))
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("licensePlate", equalTo(requestCar.licensePlate()))
                    .body("mark", equalTo(requestCar.mark()))
                    .body("colour", equalTo(requestCar.colour()))
                    .body("driverId", equalTo(TestUtils.EXISTING_DRIVER_ID.toString()));

            Car updatedCar = carRepository.findById(TestUtils.EXISTING_ID).orElseThrow();
            assertThat(updatedCar.getLicensePlate()).isEqualTo(requestCar.licensePlate());
            assertThat(updatedCar.getMark()).isEqualTo(requestCar.mark());
            assertThat(updatedCar.getColour()).isEqualTo(requestCar.colour());
            assertThat(updatedCar.getDriver().getId()).isEqualTo(TestUtils.EXISTING_DRIVER_ID);
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editCar_shouldReturnNotFound_whenCarDoesNotExist() throws Exception {
            RequestCar requestCar = CarTestEntityUtils.createTestRequestCar(TestUtils.EDIT_DRIVER_ID);
            Long carId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar)
                    .when()
                    .put(TestUtils.CAR_BY_ID_URL, carId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("message", equalTo(ExceptionMessages.CAR_NOT_FOUND.format(carId)));
        }

        @Test
        @SqlGroup({
                @Sql(scripts = "classpath:/scripts/setup-duplicate-license-plate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        })
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editCar_shouldReturnConflict_whenLicensePlateIsDuplicate() throws Exception {
            RequestCar requestCar = CarTestEntityUtils.createTestRequestCar(TestUtils.EDIT_DRIVER_ID);

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar)
                    .when()
                    .put(TestUtils.CAR_BY_ID_URL, String.valueOf(TestUtils.EDIT_ID))
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("message", equalTo(ExceptionMessages.DUPLICATE_CAR_ERROR.format("licensePlate", requestCar.licensePlate())));
        }
    }

    @Nested
    @SqlGroup({
            @Sql(scripts = "classpath:/scripts/setup-get-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:/scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    class DeleteCar {

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void deleteCar_shouldReturnNoContent() {
            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(TestUtils.CAR_BY_ID_URL, String.valueOf(TestUtils.EXISTING_ID))
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        public void deleteCar_shouldReturnNotFound_whenCarDoesNotExist() {
            Long carId = TestUtils.NON_EXISTING_ID;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete(TestUtils.CAR_BY_ID_URL, carId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("message", equalTo(ExceptionMessages.CAR_NOT_FOUND.format(carId)));
        }
    }
}