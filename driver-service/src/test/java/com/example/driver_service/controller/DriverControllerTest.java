package com.example.driver_service.controller;

import com.example.driver_service.dto.PagedResponseDriverList;
import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.service.DriverService;
import com.example.driver_service.util.ExceptionMessages;
import com.example.driver_service.utils.DriverTestEntityUtils;
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
        testResponseDriver = DriverTestEntityUtils.createTestResponseDriver();
        testRequestDriver = DriverTestEntityUtils.createTestRequestDriver();
    }

    @Nested
    class GetDriverTests {
        @Test
        void getDriverById_ShouldReturnDriver() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            when(driverService.getDriverById(driverId)).thenReturn(testResponseDriver);

            mockMvc.perform(get("/api/v1/drivers/all/{id}", driverId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(driverId))
                    .andExpect(jsonPath("$.firstName").value(testResponseDriver.firstName()))
                    .andExpect(jsonPath("$.lastName").value(testResponseDriver.lastName()));
        }

        @Test
        void getDriverByIdNonDeleted_ShouldReturnDriver() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            when(driverService.getDriverByIdNonDeleted(driverId)).thenReturn(testResponseDriver);

            mockMvc.perform(get("/api/v1/drivers/{id}", driverId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(driverId))
                    .andExpect(jsonPath("$.firstName").value(testResponseDriver.firstName()))
                    .andExpect(jsonPath("$.lastName").value(testResponseDriver.lastName()));
        }

        @Test
        void getAllDrivers_ShouldReturnPagedResponse() throws Exception {
            PageRequest pageable = PageRequest.of(0, 10);
            PagedResponseDriverList driverPage = new PagedResponseDriverList(List.of(testResponseDriver), 1, 1, 1, 10, true);

            when(driverService.getAllDrivers(pageable)).thenReturn(driverPage);

            mockMvc.perform(get("/api/v1/drivers/all").param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void getAllNonDeletedDrivers_ShouldReturnPagedResponse() throws Exception {
            PageRequest pageable = PageRequest.of(0, 10);
            PagedResponseDriverList driverPage = new PagedResponseDriverList(List.of(testResponseDriver), 1, 1, 1, 10, true);

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
        void addDriver_DuplicateEmail_ShouldReturnConflict() throws Exception {
            when(driverService.addDriver(any(RequestDriver.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email())));

            mockMvc.perform(post("/api/v1/drivers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email()))));
        }
    }

    @Nested
    class EditDriverTests {

        @Test
        void editDriver_ShouldUpdateDriverSuccessfully() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            ResponseDriver updatedResponseDriver = DriverTestEntityUtils.createUpdatedResponseDriver();
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class))).thenReturn(updatedResponseDriver);

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedResponseDriver)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(DriverTestEntityUtils.NEW_EMAIL))
                    .andExpect(jsonPath("$.phoneNumber").value(DriverTestEntityUtils.NEW_PHONE_NUMBER));
        }

        @Test
        void editDriver_InvalidData_ShouldReturnBadRequest() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            RequestDriver invalidRequestDriver = DriverTestEntityUtils.createInvalidRequestDriver();

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestDriver)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Field 'email' Invalid email format. Rejected value: " + invalidRequestDriver.email() + ";")))
                    .andExpect(content().string(containsString("Field 'phoneNumber' Invalid phone number format. Rejected value: " + invalidRequestDriver.phoneNumber() + ";")));
        }

        @Test
        void editDriver_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class)))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId))));
        }

        @Test
        void editDriver_DuplicateEmail_ShouldReturnConflict() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email())));

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email()))));
        }

        @Test
        void editDriver_DuplicatePhoneNumber_ShouldReturnConflict() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("phoneNumber", testRequestDriver.phoneNumber())));

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("phoneNumber", testRequestDriver.phoneNumber()))));
        }
    }

    @Nested
    class DeleteDriverTests {
        @Test
        void deleteDriver_ShouldReturnNoContent() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            doNothing().when(driverService).deleteDriver(driverId);

            mockMvc.perform(delete("/api/v1/drivers/{id}", driverId))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteDriver_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            doThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)))
                    .when(driverService).deleteDriver(driverId);

            mockMvc.perform(delete("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId))));
        }
    }

    @Nested
    class DeriverExistTests {
        @Test
        void doesDriverExist_ShouldReturnTrue() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            when(driverService.doesDriverExist(driverId)).thenReturn(true);

            mockMvc.perform(get("/api/v1/drivers/{id}/exists", driverId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void doesDriverExist_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            when(driverService.doesDriverExist(driverId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));

            mockMvc.perform(get("/api/v1/drivers/{id}/exists", driverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId))));
        }
    }

}