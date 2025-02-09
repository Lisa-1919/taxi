package com.modsen.driver.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driver.config.SecurityConfig;
import com.modsen.driver.controller.DriverController;
import com.modsen.driver.dto.CreateDriverRequest;
import com.modsen.driver.dto.PagedResponseDriverList;
import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.dto.ResponseDriver;
import com.modsen.driver.service.DriverService;
import com.modsen.driver.util.ExceptionMessages;
import com.modsen.driver.utils.DriverTestEntityUtils;
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
import java.util.UUID;

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
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    private UUID driverId;
    private ResponseDriver testResponseDriver;
    private RequestDriver testRequestDriver;
    private CreateDriverRequest createDriverRequest;

    @BeforeEach
    void setUp() {
        driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
        testResponseDriver = DriverTestEntityUtils.createTestResponseDriver();
        testRequestDriver = DriverTestEntityUtils.createTestRequestDriver();
        createDriverRequest = DriverTestEntityUtils.createDriverRequest();
    }

    @Nested
    class GetDriverTests {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void getDriverById_ShouldReturnDriver(boolean active) throws Exception {
            if(active) {
                when(driverService.getDriverByIdNonDeleted(driverId)).thenReturn(testResponseDriver);
            } else {
                when(driverService.getDriverById(driverId)).thenReturn(testResponseDriver);
            }


            mockMvc.perform(get("/api/v1/drivers/{id}", driverId)
                            .param("active", String.valueOf(active)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(driverId.toString()))
                    .andExpect(jsonPath("$.firstName").value(testResponseDriver.firstName()))
                    .andExpect(jsonPath("$.lastName").value(testResponseDriver.lastName()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void getAllDrivers_ShouldReturnPagedResponse(boolean active) throws Exception {
            PageRequest pageable = PageRequest.of(0, 10);
            PagedResponseDriverList driverPage = new PagedResponseDriverList(List.of(testResponseDriver), 1, 1, 1, 10, true);

            if(active) {
                when(driverService.getAllNonDeletedDrivers(pageable)).thenReturn(driverPage);
            } else {
                when(driverService.getAllDrivers(pageable)).thenReturn(driverPage);
            }

            mockMvc.perform(get("/api/v1/drivers")
                            .param("active", String.valueOf(active))
                            .param("page", "0")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addDriver_ShouldCreateDriver() throws Exception {
            when(driverService.addDriver(createDriverRequest)).thenReturn(testResponseDriver);

            mockMvc.perform(post("/api/v1/drivers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriverRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value(testResponseDriver.firstName()))
                    .andExpect(jsonPath("$.email").value(testResponseDriver.email()));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addDriver_InvalidData_ShouldReturnBadRequest() throws Exception {
            CreateDriverRequest invalidRequestDriver = DriverTestEntityUtils.invalidCreateRequestDriver();

            mockMvc.perform(post("/api/v1/drivers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestDriver)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.violations").isNotEmpty())
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'email')].message")
                            .value("Invalid email format"))
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'phoneNumber')].message")
                            .value("Invalid phone number format"));        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addDriver_DuplicateEmail_ShouldReturnConflict() throws Exception {
            when(driverService.addDriver(createDriverRequest))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email())));

            mockMvc.perform(post("/api/v1/drivers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriverRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email()))));
        }
    }

    @Nested
    class EditDriverTests {

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editDriver_ShouldUpdateDriverSuccessfully() throws Exception {
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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editDriver_InvalidData_ShouldReturnBadRequest() throws Exception {
            RequestDriver invalidRequestDriver = DriverTestEntityUtils.createInvalidRequestDriver();

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestDriver)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.violations").isNotEmpty())
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'email')].message")
                            .value("Invalid email format"))
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'phoneNumber')].message")
                            .value("Invalid phone number format"));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editDriver_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class)))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId))));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editDriver_DuplicateEmail_ShouldReturnConflict() throws Exception {
            when(driverService.editDriver(eq(driverId), any(RequestDriver.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email())));

            mockMvc.perform(put("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestDriver)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format("email", testRequestDriver.email()))));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editDriver_DuplicatePhoneNumber_ShouldReturnConflict() throws Exception {
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
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void deleteDriver_ShouldReturnNoContent() throws Exception {
            doNothing().when(driverService).deleteDriver(driverId);

            mockMvc.perform(delete("/api/v1/drivers/{id}", driverId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void deleteDriver_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            doThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)))
                    .when(driverService).deleteDriver(driverId);

            mockMvc.perform(delete("/api/v1/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId))));
        }
    }

    @Nested
    class DriverExistTests {
        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void doesDriverExist_ShouldReturnTrue() throws Exception {
            when(driverService.doesDriverExist(driverId)).thenReturn(true);

            mockMvc.perform(get("/api/v1/drivers/{id}/exists", driverId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void doesDriverExist_NonExistentDriver_ShouldReturnNotFound() throws Exception {
            when(driverService.doesDriverExist(driverId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId)));

            mockMvc.perform(get("/api/v1/drivers/{id}/exists", driverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.DRIVER_NOT_FOUND.format(driverId))));
        }
    }

}