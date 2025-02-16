package com.modsen.passenger.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception_handler.exception.GlobalExceptionHandler;
import com.modsen.passenger.config.SecurityConfig;
import com.modsen.passenger.controller.PassengerController;
import com.modsen.passenger.dto.CreatePassengerRequest;
import com.modsen.passenger.dto.PagedResponsePassengerList;
import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.service.PassengerService;
import com.modsen.passenger.util.ExceptionMessages;
import com.modsen.passenger.util.PassengerTestEntityUtils;
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

@WebMvcTest(PassengerController.class)
@ActiveProfiles("test")
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    private UUID passengerId;
    private ResponsePassenger testResponsePassenger;
    private RequestPassenger testRequestPassenger;
    private CreatePassengerRequest createPassengerRequest;

    @BeforeEach
    void setUp() {
        passengerId = PassengerTestEntityUtils.DEFAULT_PASSENGER_ID;
        testResponsePassenger = PassengerTestEntityUtils.createTestResponsePassenger();
        testRequestPassenger = PassengerTestEntityUtils.createTestRequestPassenger();
        createPassengerRequest = PassengerTestEntityUtils.createPassengerRequest();
    }

    @Nested
    class GetPassengerTests {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void getPassengerById_ShouldReturnPassenger(boolean active) throws Exception {
            if (active) {
                when(passengerService.getPassengerByIdNonDeleted(passengerId)).thenReturn(testResponsePassenger);
            } else {
                when(passengerService.getPassengerById(passengerId)).thenReturn(testResponsePassenger);
            }

            mockMvc.perform(get("/api/v1/passengers/{id}", passengerId)
                            .param("active", String.valueOf(active)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(String.valueOf(passengerId)))
                    .andExpect(jsonPath("$.firstName").value(testResponsePassenger.firstName()))
                    .andExpect(jsonPath("$.lastName").value(testResponsePassenger.lastName()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void getAllPassengers_ShouldReturnPagedResponse(boolean active) throws Exception {
            PageRequest pageable = PassengerTestEntityUtils.createDefaultPageRequest();
            PagedResponsePassengerList passengerPage = PassengerTestEntityUtils.createDefaultPagedResponsePassengerList(List.of(testResponsePassenger));

            if (active) {
                when(passengerService.getAllNonDeletedPassengers(pageable)).thenReturn(passengerPage);
            } else {
                when(passengerService.getAllPassengers(pageable)).thenReturn(passengerPage);
            }

            mockMvc.perform(get("/api/v1/passengers")
                            .param("active", String.valueOf(active))
                            .param("page", "0")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    class AddPassengerTests {

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void addPassenger_ShouldCreatePassenger() throws Exception {
            when(passengerService.addPassenger(createPassengerRequest)).thenReturn(testResponsePassenger);

            mockMvc.perform(post("/api/v1/passengers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassengerRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value(testResponsePassenger.firstName()))
                    .andExpect(jsonPath("$.email").value(testResponsePassenger.email()));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void addPassenger_InvalidData_ShouldReturnBadRequest() throws Exception {
            CreatePassengerRequest invalidRequestPassenger = PassengerTestEntityUtils.createInvalidCreateRequestPassenger();

            mockMvc.perform(post("/api/v1/passengers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestPassenger)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.violations").isNotEmpty())
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'email')].message")
                            .value("Invalid email format"))
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'phoneNumber')].message")
                            .value("Invalid phone number format"));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void addPassenger_DuplicateEmail_ShouldReturnConflict() throws Exception {
            when(passengerService.addPassenger(createPassengerRequest))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("email", testRequestPassenger.email())));

            mockMvc.perform(post("/api/v1/passengers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassengerRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("email", testRequestPassenger.email()))));
        }
    }

    @Nested
    class EditPassengerTests {

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void editPassenger_ShouldUpdatePassengerSuccessfully() throws Exception {
            when(passengerService.editPassenger(eq(passengerId), any(RequestPassenger.class))).thenReturn(testResponsePassenger);

            mockMvc.perform(put("/api/v1/passengers/{id}", passengerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestPassenger)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value(testRequestPassenger.firstName()))
                    .andExpect(jsonPath("$.lastName").value(testRequestPassenger.lastName()))
                    .andExpect(jsonPath("$.email").value(testRequestPassenger.email()));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void editPassenger_InvalidData_ShouldReturnBadRequest() throws Exception {
            RequestPassenger invalidRequestPassenger = PassengerTestEntityUtils.createInvalidRequestPassenger();

            mockMvc.perform(put("/api/v1/passengers/{id}", passengerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequestPassenger)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.violations").isNotEmpty())
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'email')].message")
                            .value("Invalid email format"))
                    .andExpect(jsonPath("$.violations[?(@.fieldName == 'phoneNumber')].message")
                            .value("Invalid phone number format"));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void editPassenger_NonExistentPassenger_ShouldReturnNotFound() throws Exception {
            when(passengerService.editPassenger(eq(passengerId), any(RequestPassenger.class)))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));

            mockMvc.perform(put("/api/v1/passengers/{id}", passengerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestPassenger)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId))));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void editPassenger_DuplicateEmail_ShouldReturnConflict() throws Exception {
            when(passengerService.editPassenger(eq(passengerId), any(RequestPassenger.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("email", testRequestPassenger.email())));

            mockMvc.perform(put("/api/v1/passengers/{id}", passengerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestPassenger)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("email", testRequestPassenger.email()))));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void editPassenger_DuplicatePhoneNumber_ShouldReturnConflict() throws Exception {
            when(passengerService.editPassenger(eq(passengerId), any(RequestPassenger.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("phoneNumber", testRequestPassenger.phoneNumber())));

            mockMvc.perform(put("/api/v1/passengers/{id}", passengerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestPassenger)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("phoneNumber", testRequestPassenger.phoneNumber()))));
        }
    }

    @Nested
    class DeletePassengerTests {
        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void deletePassenger_ShouldReturnNoContent() throws Exception {
            doNothing().when(passengerService).deletePassenger(passengerId);

            mockMvc.perform(delete("/api/v1/passengers/{id}", passengerId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void deletePassenger_NonExistentPassenger_ShouldReturnNotFound() throws Exception {
            doThrow(new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)))
                    .when(passengerService).deletePassenger(passengerId);

            mockMvc.perform(delete("/api/v1/passengers/{id}", passengerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId))));
        }
    }

    @Nested
    class PassengerExistTests {
        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void doesPassengerExist_ShouldReturnTrue() throws Exception {
            when(passengerService.doesPassengerExist(passengerId)).thenReturn(true);

            mockMvc.perform(get("/api/v1/passengers/{id}/exists", passengerId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = { "PASSENGER" }, username = "passenger@gmail.com")
        void doesPassengerExist_NonExistentPassenger_ShouldReturnNotFound() throws Exception {
            doThrow(new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)))
                    .when(passengerService).doesPassengerExist(passengerId);

            mockMvc.perform(get("/api/v1/passengers/{id}/exists", passengerId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId))));
        }
    }

}