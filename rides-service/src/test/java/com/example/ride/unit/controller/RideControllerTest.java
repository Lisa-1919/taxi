package com.example.ride.unit.controller;

import com.example.ride.controller.RideController;
import com.example.ride.dto.PagedResponseRideList;
import com.example.ride.dto.RequestChangeStatus;
import com.example.ride.dto.RequestRide;
import com.example.ride.dto.ResponseRide;
import com.example.ride.service.RideService;
import com.example.ride.util.ExceptionMessages;
import com.example.ride.util.RideStatuses;
import com.example.ride.util.RideTestEntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
        void doesRideExistForDriver_ShouldReturnOk() throws Exception {
            Long driverId = RideTestEntityUtils.DEFAULT_DRIVER_ID;

            when(rideService.doesRideExistForDriver(rideId, driverId))
                    .thenReturn(true);

            mockMvc.perform(get("/api/v1/rides/{id}/driver/{driverId}/exists", rideId, driverId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void doesRideExistForDriver_ShouldReturnDriverNotFound() throws Exception {
            Long driverId = RideTestEntityUtils.DEFAULT_DRIVER_ID;
            String errorMessage = ExceptionMessages.RIDE_NOT_FOUND_FOR_DRIVER.format(rideId, driverId);

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).doesRideExistForDriver(rideId, driverId);

            mockMvc.perform(get("/api/v1/rides/{id}/driver/{driverId}/exists", rideId, driverId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }

        @Test
        void doesRideExistForPassenger_ShouldReturnOk() throws Exception {
            Long passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;

            when(rideService.doesRideExistForPassenger(rideId, passengerId))
                    .thenReturn(true);

            mockMvc.perform(get("/api/v1/rides/{id}/passenger/{passengerId}/exists", rideId, passengerId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void doesRideExistForPassenger_ShouldReturnPassengerNotFound() throws Exception {
            Long passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;
            String errorMessage = ExceptionMessages.RIDE_NOT_FOUND_FOR_PASSENGER.format(rideId, passengerId);

            doThrow(new EntityNotFoundException(errorMessage))
                    .when(rideService).doesRideExistForPassenger(rideId, passengerId);

            mockMvc.perform(get("/api/v1/rides/{id}/passenger/{passengerId}/exists", rideId, passengerId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString(errorMessage)));
        }
    }

}