package com.modsen.ride.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception_handler.exception.GlobalExceptionHandler;
import com.modsen.ride.config.SecurityConfig;
import com.modsen.ride.controller.RideController;
import com.modsen.ride.dto.PagedResponseRideList;
import com.modsen.ride.dto.RequestChangeStatus;
import com.modsen.ride.dto.RequestRide;
import com.modsen.ride.dto.ResponseRide;
import com.modsen.ride.service.RideService;
import com.modsen.ride.util.ExceptionMessages;
import com.modsen.ride.util.RideStatuses;
import com.modsen.ride.util.RideTestEntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
@ActiveProfiles("test")
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    private Long rideId;
    private RequestRide testRequestRide;
    private ResponseRide testResponseRide;

    @BeforeEach
    void setUp() {
        rideId = RideTestEntityUtils.DEFAULT_RIDE_ID;
        testRequestRide = RideTestEntityUtils.createTestRequestRide().build();
        testResponseRide = RideTestEntityUtils.createTestResponseRide().build();
    }

    @Nested
    class GetRideTests {
        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void getRideById_ShouldReturnRide() throws Exception {
            when(rideService.getRideById(rideId))
                    .thenReturn(testResponseRide);

            mockMvc.perform(get("/api/v1/rides/{id}", rideId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(rideId))
                    .andExpect(jsonPath("$.fromAddress").value(testResponseRide.fromAddress()))
                    .andExpect(jsonPath("$.rideStatus").value(testResponseRide.rideStatus().toString()));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void getRideById_ShouldReturnNotFound() throws Exception {
            String errorMessage = ExceptionMessages.RIDE_NOT_FOUND.format(rideId);

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).getRideById(eq(rideId));

            mockMvc.perform(get("/api/v1/rides/{id}", rideId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestRide)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void getAllRides_ShouldReturnPagedResponse() throws Exception {
            Pageable pageRequest =  RideTestEntityUtils.createDefaultPageRequest();
            PagedResponseRideList ridePage = RideTestEntityUtils.createPagedResponseRideList(List.of(testResponseRide));

            when(rideService.getAllRides(pageRequest.getPageNumber(), pageRequest.getPageSize()))
                    .thenReturn(ridePage);

            mockMvc.perform(get("/api/v1/rides")
                            .param("page", "0")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    class AddRideTests {

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addRide_ShouldReturnCreated() throws Exception {
            when(rideService.addRide(any(RequestRide.class)))
                    .thenReturn(testResponseRide);

            mockMvc.perform(post("/api/v1/rides")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestRide)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(testResponseRide.id()))
                    .andExpect(jsonPath("$.rideStatus").value(testResponseRide.rideStatus().toString()));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void addRide_ShouldReturnPassengerNotFound() throws Exception {
            String errorMessage = "Passenger with id '2' not found";

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).addRide(any(RequestRide.class));

            mockMvc.perform(post("/api/v1/rides")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestRide)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }

    }

    @Nested
    class EditRideTests {

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editRide_ShouldReturnOk() throws Exception {
            when(rideService.editRide(eq(rideId), any(RequestRide.class)))
                    .thenReturn(testResponseRide);

            mockMvc.perform(put("/api/v1/rides/{id}", rideId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestRide)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testResponseRide.id()))
                    .andExpect(jsonPath("$.rideStatus").value(testResponseRide.rideStatus().toString()));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editRide_ShouldReturnRideNotFound() throws Exception {
            String errorMessage = ExceptionMessages.RIDE_NOT_FOUND.format(rideId);

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).editRide(eq(rideId), any(RequestRide.class));

            mockMvc.perform(put("/api/v1/rides/{id}", rideId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestRide)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editRide_ShouldReturnDriverNotFound() throws Exception {
            String errorMessage = "Driver with id '1' not found";

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).editRide(eq(rideId), any(RequestRide.class));

            mockMvc.perform(put("/api/v1/rides/{id}", rideId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestRide)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void editRide_ShouldReturnPassengerNotFound() throws Exception {
            String errorMessage = "Passenger with id '1' not found";

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).editRide(eq(rideId), any(RequestRide.class));

            mockMvc.perform(put("/api/v1/rides/{id}", rideId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequestRide)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void updateRideStatus_ShouldReturnOk() throws Exception {
            RequestChangeStatus requestChangeStatus = new RequestChangeStatus(RideStatuses.ACCEPTED);

            when(rideService.updateRideStatus(eq(rideId), any(RequestChangeStatus.class)))
                    .thenReturn(testResponseRide);

            mockMvc.perform(put("/api/v1/rides/{id}/status", rideId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestChangeStatus)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testResponseRide.id()))
                    .andExpect(jsonPath("$.rideStatus").value(testResponseRide.rideStatus().toString()));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void updateRideStatus_ShouldReturnRideNotFound() throws Exception {
            RequestChangeStatus requestChangeStatus = new RequestChangeStatus(RideStatuses.ACCEPTED);
            String errorMessage = ExceptionMessages.RIDE_NOT_FOUND.format(rideId);

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).updateRideStatus(eq(rideId), any(RequestChangeStatus.class));

            mockMvc.perform(put("/api/v1/rides/{id}/status", rideId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestChangeStatus)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }
    }

    @Nested
    class DoesRideExistTests {

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void doesRideExistForDriver_ShouldReturnOk() throws Exception {
            UUID driverId = RideTestEntityUtils.DEFAULT_DRIVER_ID;

            when(rideService.doesRideExistForDriver(rideId, driverId))
                    .thenReturn(true);

            mockMvc.perform(get("/api/v1/rides/{id}/driver/{driverId}/exists", rideId, driverId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void doesRideExistForDriver_ShouldReturnDriverNotFound() throws Exception {
            UUID driverId = RideTestEntityUtils.DEFAULT_DRIVER_ID;
            String errorMessage = ExceptionMessages.RIDE_NOT_FOUND_FOR_DRIVER.format(rideId, driverId);

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).doesRideExistForDriver(rideId, driverId);

            mockMvc.perform(get("/api/v1/rides/{id}/driver/{driverId}/exists", rideId, driverId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void doesRideExistForPassenger_ShouldReturnOk() throws Exception {
            UUID passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;

            when(rideService.doesRideExistForPassenger(rideId, passengerId))
                    .thenReturn(true);

            mockMvc.perform(get("/api/v1/rides/{id}/passenger/{passengerId}/exists", rideId, passengerId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(authorities = { "ROLE_DRIVER" }, username = "driver@gmail.com")
        void doesRideExistForPassenger_ShouldReturnPassengerNotFound() throws Exception {
            UUID passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;
            String errorMessage = ExceptionMessages.RIDE_NOT_FOUND_FOR_PASSENGER.format(rideId, passengerId);

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).doesRideExistForPassenger(rideId, passengerId);

            mockMvc.perform(get("/api/v1/rides/{id}/passenger/{passengerId}/exists", rideId, passengerId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }
    }

}