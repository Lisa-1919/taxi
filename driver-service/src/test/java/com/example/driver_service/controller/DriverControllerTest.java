package com.example.driver_service.controller;

import com.example.driver_service.dto.PagedResponseDriverList;
import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.service.DriverService;
import com.example.driver_service.util.ExceptionMessages;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
@ActiveProfiles("test")
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    private ResponseDriver testResponseDriver;
    private RequestDriver testRequestDriver;

    @BeforeEach
    void setUp() {
        testResponseDriver = new ResponseDriver(1L, "John", "Doe", "john@example.com", "+1234567890", "male", null, false);
        testRequestDriver = new RequestDriver("John", "Doe", "john@example.com", "+1234567890", "male");
    }

    @Nested
    class GetDriverTests {
        @Test
        void getDriverById_ShouldReturnDriver() throws Exception {
            Long driverId = 1L;
            when(driverService.getDriverById(driverId)).thenReturn(testResponseDriver);

            mockMvc.perform(get("/api/v1/drivers/all/{id}", driverId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(driverId))
                    .andExpect(jsonPath("$.firstName").value(testResponseDriver.firstName()))
                    .andExpect(jsonPath("$.lastName").value(testResponseDriver.lastName()));
        }

        @Test
        void getDriverByIdNonDeleted_ShouldReturnDriver() throws Exception {
            Long driverId = 1L;
            when(driverService.getDriverByIdNonDeleted(driverId)).thenReturn(testResponseDriver);

            mockMvc.perform(get("/api/v1/drivers/{id}", driverId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(driverId))
                    .andExpect(jsonPath("$.firstName").value(testResponseDriver.firstName()))
                    .andExpect(jsonPath("$.lastName").value(testResponseDriver.lastName()));
        }

        @Test
        void getAllDrivers_ShouldReturnPagedResponse() throws Exception {
            var pageable = PageRequest.of(0, 10);
            var driverPage = new PagedResponseDriverList(List.of(testResponseDriver), 1, 1, 1, 10, true);

            when(driverService.getAllDrivers(pageable)).thenReturn(driverPage);

            mockMvc.perform(get("/api/v1/drivers/all").param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void getAllNonDeletedDrivers_ShouldReturnPagedResponse() throws Exception {
            var pageable = PageRequest.of(0, 10);
            var driverPage = new PagedResponseDriverList(List.of(testResponseDriver), 1, 1, 1, 10, true);

            when(driverService.getAllNonDeletedDrivers(pageable)).thenReturn(driverPage);

            mockMvc.perform(get("/api/v1/drivers").param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    class AddDriverTests {

        @Test
        void addDriver_ShouldCreateDriver() throws Exception {
            when(driverService.addDriver(any(RequestDriver.class))).thenReturn(testResponseDriver);

            mockMvc.perform(post("/api/v1/drivers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value(testResponseDriver.firstName()))
                    .andExpect(jsonPath("$.email").value(testResponseDriver.email()));
        }

        @Test
        void addDriver_InvalidData_ShouldReturnBadRequest() throws Exception {
            RequestDriver invalidRequestDriver = new RequestDriver(testRequestDriver.firstName(), testRequestDriver.lastName(), "invalid-email", testRequestDriver.phoneNumber(), testRequestDriver.sex());

            mockMvc.perform(post("/api/v1/drivers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestDriver)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Field 'email' Invalid email format. Rejected value: invalid-email;")));
        }

        @Test
        void addDriver_DuplicateEmail_ShouldReturnConflict() throws Exception{
            when(driverService.addDriver(any(RequestDriver.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email())));

            mockMvc.perform(post("/api/v1/drivers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString("A driver with email 'john@example.com' already exists")));
        }
    }

    @Nested
    class EditDriverTests {

        @Test
        void editDriver_ShouldUpdateDriverSuccessfully() throws Exception {
            Long driverId = 1L;
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class))).thenReturn(testResponseDriver);

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.email").value("john@example.com"));
        }

        @Test
        void editDriver_InvalidData_ShouldReturnBadRequest() throws Exception {
            RequestDriver invalidRequestDriver = new RequestDriver("J", "", "invalid-email", "123", "male");

            mockMvc.perform(put("/api/v1/drivers/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestDriver)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Field 'lastName' Last name must be between 1 and 50 characters")))
                    .andExpect(content().string(containsString("Field 'email' Invalid email format. Rejected value: invalid-email;")))
                    .andExpect(content().string(containsString("Field 'phoneNumber' Invalid phone number format. Rejected value: 123;")));
        }

        @Test
        void editDriver_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            Long driverId = 999L;
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class)))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Driver with id '" + driverId + "' not found")));
        }

        @Test
        void editDriver_DuplicateEmail_ShouldReturnConflict() throws Exception {
            Long driverId = 1L;
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email())));

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString("A driver with email 'john@example.com' already exists")));
        }

        @Test
        void editDriver_DuplicatePhoneNumber_ShouldReturnConflict() throws Exception {
            Long driverId = 1L;
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("phoneNumber", testRequestDriver.phoneNumber())));

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString("A driver with phoneNumber '+1234567890' already exists")));
        }
    }

    @Nested
    class DeleteDriverTests {
        @Test
        void deleteDriver_ShouldReturnNoContent() throws Exception {
            Long driverId = 1L;
            doNothing().when(driverService).deleteDriver(driverId);

            mockMvc.perform(delete("/api/v1/drivers/{id}", driverId))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteDriver_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            Long nonExistentDriverId = 999L;
            doThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(nonExistentDriverId)))
                    .when(driverService).deleteDriver(nonExistentDriverId);

            mockMvc.perform(delete("/api/v1/drivers/{id}", nonExistentDriverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Driver with id '" + nonExistentDriverId + "' not found")));
        }
    }

    @Nested
    class DeriverExistTests {
        @Test
        void doesDriverExist_ShouldReturnTrue() throws Exception {
            Long driverId = 1L;
            when(driverService.doesDriverExist(driverId)).thenReturn(true);

            mockMvc.perform(get("/api/v1/drivers/{id}/exists", driverId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void doesDriverExist_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            Long nonExistentDriverId = 999L;
            when(driverService.doesDriverExist(nonExistentDriverId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(nonExistentDriverId)));

            mockMvc.perform(get("/api/v1/drivers/{id}/exists", nonExistentDriverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Driver with id '" + nonExistentDriverId + "' not found")));
        }
    }

}