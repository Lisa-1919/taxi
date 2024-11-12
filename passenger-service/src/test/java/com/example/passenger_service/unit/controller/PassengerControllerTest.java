package com.example.passenger_service.unit.controller;

import com.example.passenger_service.controller.PassengerController;
import com.example.passenger_service.dto.PagedResponsePassengerList;
import com.example.passenger_service.dto.RequestPassenger;
import com.example.passenger_service.dto.ResponsePassenger;
import com.example.passenger_service.service.PassengerService;
import com.example.passenger_service.util.ExceptionMessages;
import com.example.passenger_service.util.PassengerTestEntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

@WebMvcTest(PassengerController.class)
@ActiveProfiles("test")
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    private Long passengerId;
    private ResponsePassenger testResponsePassenger;
    private RequestPassenger testRequestPassenger;

    @BeforeEach
    void setUp() {
        passengerId = PassengerTestEntityUtils.DEFAULT_PASSENGER_ID;
        testResponsePassenger = PassengerTestEntityUtils.createTestResponsePassenger();
        testRequestPassenger = PassengerTestEntityUtils.createTestRequestPassenger();
    }

    @Nested
    class GetPassengerTests {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getPassengerById_ShouldReturnPassenger(boolean active) throws Exception {
            if (active) {
                when(passengerService.getPassengerByIdNonDeleted(passengerId)).thenReturn(testResponsePassenger);
            } else {
                when(passengerService.getPassengerById(passengerId)).thenReturn(testResponsePassenger);
            }

            mockMvc.perform(get("/api/v1/passengers/{id}", passengerId)
                            .param("active", String.valueOf(active)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(passengerId))
                    .andExpect(jsonPath("$.firstName").value(testResponsePassenger.firstName()))
                    .andExpect(jsonPath("$.lastName").value(testResponsePassenger.lastName()));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
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
        void addPassenger_ShouldCreatePassenger() throws Exception {
            when(passengerService.addPassenger(any(RequestPassenger.class))).thenReturn(testResponsePassenger);

            mockMvc.perform(post("/api/v1/passengers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestPassenger)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value(testResponsePassenger.firstName()))
                    .andExpect(jsonPath("$.email").value(testResponsePassenger.email()));
        }

        @Test
        void addPassenger_InvalidData_ShouldReturnBadRequest() throws Exception {
            RequestPassenger invalidRequestPassenger = PassengerTestEntityUtils.createInvalidRequestPassenger();

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
        void addPassenger_DuplicateEmail_ShouldReturnConflict() throws Exception {
            when(passengerService.addPassenger(any(RequestPassenger.class)))
                    .thenThrow(new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("email", testRequestPassenger.email())));

            mockMvc.perform(post("/api/v1/passengers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestPassenger)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format("email", testRequestPassenger.email()))));
        }
    }

    @Nested
    class EditPassengerTests {

        @Test
        void editPassenger_ShouldUpdatePassengerSuccessfully() throws Exception {
            when(passengerService.editPassenger(eq(passengerId), any(RequestPassenger.class))).thenReturn(testResponsePassenger);

            mockMvc.perform(put("/api/v1/passengers/{id}", passengerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestPassenger)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value(PassengerTestEntityUtils.DEFAULT_FIRST_NAME))
                    .andExpect(jsonPath("$.lastName").value(PassengerTestEntityUtils.DEFAULT_LAST_NAME))
                    .andExpect(jsonPath("$.email").value(PassengerTestEntityUtils.DEFAULT_EMAIL));
        }

        @Test
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
        void deletePassenger_ShouldReturnNoContent() throws Exception {
            doNothing().when(passengerService).deletePassenger(passengerId);

            mockMvc.perform(delete("/api/v1/passengers/{id}", passengerId))
                    .andExpect(status().isNoContent());
        }

        @Test
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
        void doesPassengerExist_ShouldReturnTrue() throws Exception {
            when(passengerService.doesPassengerExist(passengerId)).thenReturn(true);

            mockMvc.perform(get("/api/v1/passengers/{id}/exists", passengerId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void doesPassengerExist_NonExistentPassenger_ShouldReturnNotFound() throws Exception {
            when(passengerService.doesPassengerExist(passengerId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId)));

            mockMvc.perform(get("/api/v1/passengers/{id}/exists", passengerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(ExceptionMessages.PASSENGER_NOT_FOUND.format(passengerId))));
        }
    }

}