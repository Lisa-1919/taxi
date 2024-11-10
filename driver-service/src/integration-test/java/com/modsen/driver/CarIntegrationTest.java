package com.modsen.driver;

import com.modsen.driver.dto.RequestCar;
import com.modsen.driver.entity.Car;
import com.modsen.driver.entity.Driver;
import com.modsen.driver.mapper.CarMapper;
import com.modsen.driver.repo.CarRepository;
import com.modsen.driver.repo.DriverRepository;
import com.modsen.driver.util.ExceptionMessages;
import com.modsen.driver.utils.CarTestEntityUtils;
import com.modsen.driver.utils.DriverTestEntityUtils;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration-test")
public class CarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private CarMapper carMapper;

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
    class GetCar {

        @Autowired
        private CarRepository carRepository;
        @Autowired
        private DriverRepository driverRepository;

        private Car nonDeletedCar;
        private Driver nonDeletedDriver;

        @BeforeEach
        private void setUp() {
            nonDeletedDriver = new Driver(null, "John", "Doe", "testemail32@example.com", "+123456700032", "male", null, false);
            driverRepository.save(nonDeletedDriver);

            nonDeletedCar = new Car(null, "AB 1234-7", "mark", "colour", nonDeletedDriver, false);
            carRepository.save(nonDeletedCar);
        }

        @Transactional
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getCarById_shouldReturnCar(boolean active) {
            RestAssuredMockMvc.given()
                    .param("active", active)
                    .when()
                    .get("/api/v1/cars/" + nonDeletedCar.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(nonDeletedCar.getId().intValue()))
                    .body("licensePlate", equalTo(nonDeletedCar.getLicensePlate()));
        }

        @Transactional
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getCarById_shouldReturnNotFound(boolean active) {
            Long carId = 999L;

            RestAssuredMockMvc.given()
                    .param("active", active)
                    .when()
                    .get("/api/v1/cars/" + carId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.CAR_NOT_FOUND.format(carId)));
        }

        @Transactional
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        public void getAllCars_shouldReturnPagedCars(boolean active) {
            RestAssuredMockMvc.given()
                    .param("active", active)
                    .param("page", 0)
                    .param("size", 10)
                    .when()
                    .get("/api/v1/cars")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("cars", hasSize(1))
                    .body("totalElements", equalTo(1));
        }
    }

    @Nested
    class AddCar {

        @Transactional
        @Test
        void addCar_shouldReturnCreatedCar() throws Exception {
            Driver driver =  DriverTestEntityUtils.createTestDriver();
            driver.setId(null);
            driverRepository.save(driver);

            RequestCar requestCar = CarTestEntityUtils.createTestRequestCar(driver.getId());

            Integer carId = RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar)
                    .when()
                    .post("/api/v1/cars")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("licensePlate", equalTo(requestCar.licensePlate()))
                    .body("driverId", equalTo(driver.getId().intValue()))
                    .extract()
                    .path("id");

            Car savedCar = carRepository.findById(Long.valueOf(carId)).orElseThrow();
            assertThat(savedCar.getLicensePlate()).isEqualTo(requestCar.licensePlate());
            assertThat(savedCar.getDriver().getId()).isEqualTo(driver.getId());

            Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
            assertThat(updatedDriver.getCar()).isEqualTo(savedCar);
        }

        @Transactional
        @Test
        public void addCar_shouldReturnBadRequest_whenLicensePlateIsDuplicate() throws Exception {
            Driver driver1 = new Driver(null, "John", "Doe", "testemail43@example.com", "+123456700043", "male", null, false);
            Driver driver2 = new Driver(null, "John", "Doe", "testemail44@example.com", "+123456700044", "male", null, false);
            driverRepository.saveAll(List.of(driver1, driver2));
            RequestCar requestCar1 = new RequestCar("AB 1239-0", "mark", "colour", driver1.getId());
            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar1)
                    .when()
                    .post("/api/v1/cars")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            RequestCar requestCar2 = new RequestCar("AB 1239-0", "mark", "colour", driver2.getId());
            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar2)
                    .when()
                    .post("/api/v1/cars")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DUPLICATE_CAR_ERROR.format("licensePlate", requestCar2.licensePlate())));
        }

        @Transactional
        @Test
        public void addCar_shouldReturnNotFound_whenDriverNotFound() throws Exception {
            Long driverId = 999L;
            RequestCar requestCar = new RequestCar("AB 1239-1", "mark", "colour", driverId);
            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar)
                    .when()
                    .post("/api/v1/cars")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));
        }
    }

    @Nested
    class EditCar {

        @Test
        @Transactional
        void editCar_shouldReturnUpdatedCar() throws Exception {
            Driver driver = DriverTestEntityUtils.createTestDriver();
            driver.setId(null);
            driverRepository.save(driver);

            Car car = CarTestEntityUtils.createTestCar();
            car.setId(null);
            car.setDriver(driver);
            Car savedCar = carRepository.save(car);

            RequestCar updatedRequestCar = new RequestCar("CD 5678-0", "updatedMark", "updatedColour", driver.getId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(updatedRequestCar)
                    .when()
                    .put("/api/v1/cars/" + savedCar.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("licensePlate", equalTo(updatedRequestCar.licensePlate()))
                    .body("mark", equalTo(updatedRequestCar.mark()))
                    .body("colour", equalTo(updatedRequestCar.colour()))
                    .body("driverId", equalTo(driver.getId().intValue()));

            Car updatedCar = carRepository.findById(savedCar.getId()).orElseThrow();
            assertThat(updatedCar.getLicensePlate()).isEqualTo(updatedRequestCar.licensePlate());
            assertThat(updatedCar.getMark()).isEqualTo(updatedRequestCar.mark());
            assertThat(updatedCar.getColour()).isEqualTo(updatedRequestCar.colour());
            assertThat(updatedCar.getDriver().getId()).isEqualTo(driver.getId());
        }

        @Transactional
        @Test
        void editCar_shouldReturnNotFoundWhenCarDoesNotExist() throws Exception {
            Driver driver = DriverTestEntityUtils.createTestDriver();
            driver.setId(null);
            driverRepository.save(driver);

            RequestCar updatedRequestCar = new RequestCar("CD 5678-1", "updatedMark", "updatedColour", driver.getId());
            Long carId = 999L;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(updatedRequestCar)
                    .when()
                    .put("/api/v1/cars/" + carId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.CAR_NOT_FOUND.format(carId)));
        }

        @Transactional
        @Test
        void editCar_shouldReturnConflictWhenLicensePlateIsDuplicate() throws Exception {
            Driver driver1 = new Driver(null, "John", "Doe", "testemail44@example.com", "+123456700044", "male", null, false);
            Driver driver2 = new Driver(null, "Jane", "Doe", "testemail45@example.com", "+123456700045", "female", null, false);
            driverRepository.save(driver1);
            driverRepository.save(driver2);

            RequestCar requestCar1 = new RequestCar("AB 1111-1", "mark1", "colour1", driver1.getId());
            RequestCar requestCar2 = new RequestCar("AB 1111-2", "mark1", "colour1", driver2.getId());
            carRepository.save(carMapper.requestCarToCar(requestCar1));
            Car savedCar1 = carRepository.save(carMapper.requestCarToCar(requestCar2));

            RequestCar requestCar3 = new RequestCar("AB 1111-1", "mark2", "colour2", driver2.getId());

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestCar3)
                    .when()
                    .put("/api/v1/cars/" + savedCar1.getId())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body(equalTo(ExceptionMessages.DUPLICATE_CAR_ERROR.format("licensePlate", requestCar3.licensePlate())));
        }
    }

    @Nested
    class DeleteCar {
        @Transactional
        @Test
        public void deleteCar_shouldReturnNoContent() {
            Driver driver = new Driver(null, "John", "Doe", "testemail4@example.com", "+123456700049", "male", null, false);
            driverRepository.save(driver);

            Car car = CarTestEntityUtils.createTestCar();
            car.setId(null);
            car.setDriver(driver);
            carRepository.save(car);
            Long carId = car.getId();

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/api/v1/cars/" + carId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Transactional
        @Test
        public void deleteCar_shouldReturnNotFound() {
            Long carId = 999L;

            RestAssuredMockMvc.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/api/v1/cars/" + carId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(equalTo(ExceptionMessages.CAR_NOT_FOUND.format(carId)));
        }
    }
}
