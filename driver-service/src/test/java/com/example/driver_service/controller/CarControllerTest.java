package com.example.driver_service.controller;

import com.example.driver_service.dto.PagedResponseCarList;
import com.example.driver_service.dto.RequestCar;
import com.example.driver_service.dto.ResponseCar;
import com.example.driver_service.service.CarService;
import com.example.driver_service.util.ExceptionMessages;
import com.example.driver_service.utils.CarTestEntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
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
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    private ResponseCar testResponseCar;
    private RequestCar testRequestCar;

    @BeforeEach
    void setUp() {
        testRequestCar = CarTestEntityUtils.createTestRequestCar();
        testResponseCar = CarTestEntityUtils.createTestResponseCar();
    }

    @Nested
    class GetCarTests {
        @Test
        void getCarById_ShouldReturnCar() throws Exception {
            Long carId = CarTestEntityUtils.DEFAULT_CAR_ID;
            when(carService.getCarById(carId)).thenReturn(testResponseCar);

            mockMvc.perform(get("/api/v1/cars/all/{id}", carId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(carId))
                    .andExpect(jsonPath("$.licensePlate").value(testResponseCar.licensePlate()))
                    .andExpect(jsonPath("$.mark").value(testResponseCar.mark()));
        }

        @Test
        void getCarByIdNonDeleted_ShouldReturnCar() throws Exception {
            Long carId = CarTestEntityUtils.DEFAULT_CAR_ID;
            when(carService.getCarByIdNonDeleted(carId)).thenReturn(testResponseCar);

            mockMvc.perform(get("/api/v1/cars/{id}", carId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(carId))
                    .andExpect(jsonPath("$.licensePlate").value(testResponseCar.licensePlate()))
                    .andExpect(jsonPath("$.mark").value(testResponseCar.mark()));
        }

        @Test
        void getAllNonDeletedCars_ShouldReturnPagedResponse() throws Exception {
            PageRequest pageable = CarTestEntityUtils.createDefaultPageRequest();
            PagedResponseCarList carPage = CarTestEntityUtils.createDefaultPagedResponseCarList(List.of(testResponseCar));

            when(carService.getAllNonDeletedCars(pageable)).thenReturn(carPage);

            mockMvc.perform(get("/api/v1/cars").param("page", String.valueOf(CarTestEntityUtils.DEFAULT_PAGE_NUMBER)).param("size", String.valueOf(CarTestEntityUtils.DEFAULT_PAGE_SIZE)))
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.totalElements").value(CarTestEntityUtils.DEFAULT_TOTAL_ELEMENTS));
        }
    }

    @Nested
    class AddCarTests {
        @Test
        void addCar_ShouldCreateCarSuccessfully() throws Exception {
            when(carService.addCar(any(RequestCar.class))).thenReturn(testResponseCar);

            mockMvc.perform(post("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestCar)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.licensePlate").value(testResponseCar.licensePlate()))
                    .andExpect(jsonPath("$.mark").value(testResponseCar.mark()))
                    .andExpect(jsonPath("$.colour").value(testResponseCar.colour()))
                    .andExpect(jsonPath("$.driverId").value(testResponseCar.driverId()));
        }

        @Test
        void addCar_InvalidData_ShouldReturnBadRequest() throws Exception {
            RequestCar invalidRequestCar = CarTestEntityUtils.createInvalidRequestCar();

            mockMvc.perform(post("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestCar)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Field 'licensePlate' Invalid license plate format. Rejected value: " + invalidRequestCar.licensePlate() + ";")))
                    .andExpect(content().string(containsString("Field 'mark' Mark cannot be null. Rejected value: " + invalidRequestCar.mark() + ";")))
                    .andExpect(content().string(containsString("Field 'driverId' Driver id cannot be null. Rejected value: " + invalidRequestCar.driverId() + ";")));
        }

        @Test
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
        void addCar_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            Long nonExistentDriverId = CarTestEntityUtils.DEFAULT_DRIVER_ID;

            when(carService.addCar(any(RequestCar.class)))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(nonExistentDriverId)));

            mockMvc.perform(post("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestCar)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.DRIVER_NOT_FOUND.format(nonExistentDriverId))));
        }

        @Nested
        class EditCarTests {

            @Test
            void editCar_ShouldUpdateCarSuccessfully() throws Exception {
                Long carId = CarTestEntityUtils.DEFAULT_CAR_ID;
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
            void editCar_InvalidData_ShouldReturnBadRequest() throws Exception {
                Long carId = CarTestEntityUtils.DEFAULT_CAR_ID;
                RequestCar invalidRequestCar = CarTestEntityUtils.createInvalidRequestCar();

                mockMvc.perform(put("/api/v1/cars/{id}", carId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequestCar)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("Field 'licensePlate' Invalid license plate format. Rejected value: " + invalidRequestCar.licensePlate() + ";")))
                        .andExpect(content().string(containsString("Field 'mark' Mark cannot be null. Rejected value: " + invalidRequestCar.mark() + ";")))
                        .andExpect(content().string(containsString("Field 'driverId' Driver id cannot be null. Rejected value: " + invalidRequestCar.driverId() + ";")));
            }

            @Test
            void editCar_NonExistentCar_ShouldReturnNotFound() throws Exception {
                Long carId = CarTestEntityUtils.DEFAULT_CAR_ID;
                when(carService.editCar(eq(carId), any(RequestCar.class)))
                        .thenThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(carId)));

                mockMvc.perform(put("/api/v1/cars/{id}", carId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRequestCar)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().string(containsString(ExceptionMessages.CAR_NOT_FOUND.format(carId))));
            }

            @Test
            void editCar_DuplicateLicensePlate_ShouldReturnConflict() throws Exception {
                Long carId = CarTestEntityUtils.DEFAULT_CAR_ID;
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
            void deleteCar_ShouldReturnNoContent() throws Exception {
                Long carId = CarTestEntityUtils.DEFAULT_CAR_ID;
                doNothing().when(carService).deleteCar(carId);

                mockMvc.perform(delete("/api/v1/cars/{id}", carId))
                        .andExpect(status().isNoContent());
            }

            @Test
            void deleteCar_NonExistentCar_ShouldReturnNotFound() throws Exception {
                Long carId = CarTestEntityUtils.DEFAULT_CAR_ID;
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