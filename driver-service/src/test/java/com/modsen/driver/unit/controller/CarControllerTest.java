package com.modsen.driver.unit.controller;

import com.modsen.driver.config.SecurityConfig;
import com.modsen.driver.controller.CarController;
import com.modsen.driver.dto.PagedResponseCarList;
import com.modsen.driver.dto.RequestCar;
import com.modsen.driver.dto.ResponseCar;
import com.modsen.driver.service.CarService;
import com.modsen.driver.util.ExceptionMessages;
import com.modsen.driver.utils.CarTestEntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception_handler.exception.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
@ActiveProfiles("test")
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    private Long carId;
    private ResponseCar testResponseCar;
    private RequestCar testRequestCar;

    @BeforeEach
    void setUp() {
        carId = CarTestEntityUtils.DEFAULT_CAR_ID;
        testRequestCar = CarTestEntityUtils.createTestRequestCar();
        testResponseCar = CarTestEntityUtils.createTestResponseCar();
    }

    @Nested
    class GetCarTests {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void getCarById_ShouldReturnCar(boolean active) throws Exception {
            if(active) {
                when(carService.getCarByIdNonDeleted(carId)).thenReturn(testResponseCar);
            } else {
                when(carService.getCarById(carId)).thenReturn(testResponseCar);
            }

            mockMvc.perform(get("/api/v1/cars/{id}", carId)
                            .param("active", String.valueOf(active)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(carId))
                    .andExpect(jsonPath("$.licensePlate").value(testResponseCar.licensePlate()))
                    .andExpect(jsonPath("$.mark").value(testResponseCar.mark()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void getAllCars_ShouldReturnPagedResponse(boolean active) throws Exception {
            PageRequest pageable = CarTestEntityUtils.createDefaultPageRequest();
            PagedResponseCarList carPage = CarTestEntityUtils.createDefaultPagedResponseCarList(List.of(testResponseCar));

            if(active) {
                when(carService.getAllNonDeletedCars(pageable)).thenReturn(carPage);
            } else {
                when(carService.getAllCars(pageable)).thenReturn(carPage);
            }

            mockMvc.perform(get("/api/v1/cars")
                            .param("active", String.valueOf(active))
                            .param("page", "0")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(CarTestEntityUtils.DEFAULT_TOTAL_ELEMENTS));
        }
    }

    @Nested
    class AddCarTests {
        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addCar_ShouldCreateCarSuccessfully() throws Exception {
            when(carService.addCar(any(RequestCar.class))).thenReturn(testResponseCar);

            mockMvc.perform(post("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestCar)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.licensePlate").value(testResponseCar.licensePlate()))
                    .andExpect(jsonPath("$.mark").value(testResponseCar.mark()))
                    .andExpect(jsonPath("$.colour").value(testResponseCar.colour()))
                    .andExpect(jsonPath("$.driverId").value(testResponseCar.driverId().toString()));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addCar_InvalidData_ShouldReturnBadRequest() throws Exception {
            RequestCar invalidRequestCar = CarTestEntityUtils.createInvalidRequestCar();

            mockMvc.perform(post("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestCar)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.violations").isNotEmpty())
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'licensePlate')].message")
                            .value("Invalid license plate format"))
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'mark')].message")
                            .value("Mark cannot be null"))
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'driverId')].message")
                            .value("Driver id cannot be null"));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addCar_DuplicateLicensePlate_ShouldReturnConflict() throws Exception {
            when(carService.addCar(any(RequestCar.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_CAR_ERROR.format("licensePlate", testRequestCar.licensePlate())));

            mockMvc.perform(post("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestCar)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_CAR_ERROR.format("licensePlate", testRequestCar.licensePlate()))));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addCar_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            when(carService.addCar(any(RequestCar.class)))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(carId)));

            mockMvc.perform(post("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestCar)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.DRIVER_NOT_FOUND.format(carId))));
        }

        @Nested
        class EditCarTests {

            @Test
            @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
            void editCar_ShouldUpdateCarSuccessfully() throws Exception {
                when(carService.editCar(eq(carId), any(RequestCar.class))).thenReturn(testResponseCar);

                mockMvc.perform(put("/api/v1/cars/{id}", carId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRequestCar)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.licensePlate").value(testResponseCar.licensePlate()))
                        .andExpect(jsonPath("$.mark").value(testResponseCar.mark()))
                        .andExpect(jsonPath("$.colour").value(testResponseCar.colour()));
            }

            @Test
            @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
            void editCar_InvalidData_ShouldReturnBadRequest() throws Exception {
                RequestCar invalidRequestCar = CarTestEntityUtils.createInvalidRequestCar();

                mockMvc.perform(put("/api/v1/cars/{id}", carId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequestCar)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.violations").isNotEmpty())
                        .andExpect(jsonPath("$.violations[?(@.fieldName == 'licensePlate')].message")
                                .value("Invalid license plate format"))
                        .andExpect(jsonPath("$.violations[?(@.fieldName == 'mark')].message")
                                .value("Mark cannot be null"))
                        .andExpect(jsonPath("$.violations[?(@.fieldName == 'driverId')].message")
                                .value("Driver id cannot be null"));
            }

            @Test
            @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
            void editCar_NonExistentCar_ShouldReturnNotFound() throws Exception {
                when(carService.editCar(eq(carId), any(RequestCar.class)))
                        .thenThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(carId)));

                mockMvc.perform(put("/api/v1/cars/{id}", carId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRequestCar)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().string(containsString(ExceptionMessages.CAR_NOT_FOUND.format(carId))));
            }

            @Test
            @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
            void editCar_DuplicateLicensePlate_ShouldReturnConflict() throws Exception {
                when(carService.editCar(eq(carId), any(RequestCar.class)))
                        .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_CAR_ERROR.format("licensePlate", testRequestCar.licensePlate())));

                mockMvc.perform(put("/api/v1/cars/{id}", carId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRequestCar)))
                        .andExpect(status().isConflict())
                        .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_CAR_ERROR.format("licensePlate", testRequestCar.licensePlate()))));
            }
        }

        @Nested
        class DeleteCarTests {
            @Test
            @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
            void deleteCar_ShouldReturnNoContent() throws Exception {
                doNothing().when(carService).deleteCar(carId);

                mockMvc.perform(delete("/api/v1/cars/{id}", carId))
                        .andExpect(status().isNoContent());
            }

            @Test
            @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
            void deleteCar_NonExistentCar_ShouldReturnNotFound() throws Exception {
                doThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(carId)))
                        .when(carService).deleteCar(carId);

                mockMvc.perform(delete("/api/v1/cars/{id}", carId)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().string(containsString(ExceptionMessages.CAR_NOT_FOUND.format(carId))));
            }
        }
    }

}