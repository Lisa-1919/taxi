package com.example.rides_service.unit.service;

import com.example.rides_service.client.DriverServiceClient;
import com.example.rides_service.client.PassengerServiceClient;
import com.example.rides_service.dto.PagedResponseRideList;
import com.example.rides_service.dto.RequestChangeStatus;
import com.example.rides_service.dto.RequestRide;
import com.example.rides_service.dto.ResponseRide;
import com.example.rides_service.dto.UpdateStatusMessage;
import com.example.rides_service.entity.Ride;
import com.example.rides_service.exception.InvalidStatusTransitionException;
import com.example.rides_service.mapper.RideMapper;
import com.example.rides_service.repo.RideRepository;
import com.example.rides_service.service.KafkaProducer;
import com.example.rides_service.service.RideServiceImpl;
import com.example.rides_service.util.ExceptionMessages;
import com.example.rides_service.util.RideStatuses;
import com.example.rides_service.util.RideTestEntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {

    @Mock
    private RideRepository rideRepository;
    @Mock
    private RideMapper rideMapper;
    @Mock
    private DriverServiceClient driverServiceClient;
    @Mock
    private PassengerServiceClient passengerServiceClient;
    @Mock
    private KafkaProducer kafkaProducer;
    @InjectMocks
    private RideServiceImpl rideService;

    private Long rideId;
    private Ride testRide;
    private RequestRide testRequestRide;
    private ResponseRide testResponseRide;

    @BeforeEach
    void setUp() {
        rideId = RideTestEntityUtils.DEFAULT_RIDE_ID;
        testRide = RideTestEntityUtils.createTestRide();
        testRequestRide = RideTestEntityUtils.createTestRequestRide();
        testResponseRide = RideTestEntityUtils.createTestResponseRide();
    }

    @Nested
    class AddRideTests {
        @Test
        void addRideOk() {
            when(rideMapper.requestRideToRide(any(RequestRide.class))).thenReturn(testRide);
            when(rideRepository.save(any(Ride.class))).thenReturn(testRide);
            when(rideMapper.rideToResponseRide(any(Ride.class))).thenReturn(testResponseRide);

            ResponseRide result = rideService.addRide(testRequestRide);

            verify(passengerServiceClient).doesPassengerExists(testRequestRide.passengerId());
            verify(rideRepository).save(testRide);
            verify(rideMapper).rideToResponseRide(testRide);
            assertEquals(testResponseRide, result);
        }

        @Test
        void addRidePassengerNotFound() {
            doThrow(EntityNotFoundException.class)
                    .when(passengerServiceClient).doesPassengerExists(testRequestRide.passengerId());

            assertThrows(EntityNotFoundException.class, () -> rideService.addRide(testRequestRide));

            verify(passengerServiceClient).doesPassengerExists(testRequestRide.passengerId());

            verifyNoMoreInteractions(rideRepository, rideMapper);
        }
    }

    @Nested
    class EditRideTests {
        @Test
        void editRideOk() {
            RequestRide requestRide = RideTestEntityUtils.createUpdateRequestRide();
            Ride updatedRide =  RideTestEntityUtils.createTestUpdatedRide();
            ResponseRide responseRide = RideTestEntityUtils.createTestUpdatedResponseRide();
            when(rideRepository.findById(rideId)).thenReturn(Optional.of(testRide));
            when(rideRepository.save(testRide)).thenReturn(updatedRide);
            when(rideMapper.rideToResponseRide(updatedRide)).thenReturn(responseRide);

            ResponseRide result = rideService.editRide(rideId, requestRide);

            assertEquals(responseRide, result);
            verify(driverServiceClient).doesDriverExists(requestRide.driverId());
            verify(passengerServiceClient).doesPassengerExists(requestRide.passengerId());
            verify(rideMapper).updateRideFromRideDto(requestRide, testRide);
            verify(rideRepository).save(testRide);
            verify(kafkaProducer).send(any(UpdateStatusMessage.class));

        }

        @Test
        void editRidePassengerNotFound() {
            RequestRide requestRide = RideTestEntityUtils.createUpdateRequestRide();

            doThrow(EntityNotFoundException.class)
                    .when(passengerServiceClient).doesPassengerExists(requestRide.passengerId());

            assertThrows(EntityNotFoundException.class, () -> rideService.editRide(rideId, requestRide));

            verify(driverServiceClient).doesDriverExists(requestRide.driverId());
            verify(passengerServiceClient).doesPassengerExists(requestRide.passengerId());

            verifyNoMoreInteractions(rideRepository, rideMapper);

        }

        @Test
        void editRideDriverNotFound() {
            RequestRide requestRide = RideTestEntityUtils.createUpdateRequestRide();

            doThrow(EntityNotFoundException.class)
                    .when(driverServiceClient).doesDriverExists(requestRide.driverId());

            assertThrows(EntityNotFoundException.class, () -> rideService.editRide(rideId, requestRide));

            verify(driverServiceClient).doesDriverExists(requestRide.driverId());

            verifyNoMoreInteractions(rideRepository, rideMapper);
        }

        @Test
        void editRideNotFound() {
            RequestRide requestRide = RideTestEntityUtils.createUpdateRequestRide();
            when(rideRepository.findById(rideId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(rideId)));

            assertThrows(EntityNotFoundException.class, () -> rideService.editRide(rideId, requestRide));

            verify(passengerServiceClient).doesPassengerExists(requestRide.passengerId());
            verify(driverServiceClient).doesDriverExists(requestRide.driverId());
            verify(rideRepository).findById(rideId);
            verifyNoMoreInteractions(rideRepository, rideMapper);
        }

        @Test
        void updateRideStatusOk() {
            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.CANCELED);
            testRide.setRideStatus(RideStatuses.CREATED);

            when(rideRepository.findById(rideId)).thenReturn(Optional.of(testRide));
            when(rideRepository.save(any(Ride.class))).thenReturn(testRide);
            when(rideMapper.rideToResponseRide(any(Ride.class))).thenReturn(testResponseRide);

            ResponseRide result = rideService.updateRideStatus(rideId, requestChangeStatus);

            verify(rideRepository).save(testRide);
            verify(kafkaProducer).send(any(UpdateStatusMessage.class));
            assertEquals(testResponseRide, result);
        }

        @Test
        void updateRideStatusRideNotFound() {
            when(rideRepository.findById(rideId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(rideId)));

            assertThrows(EntityNotFoundException.class, () -> rideService.updateRideStatus(rideId, any(RequestChangeStatus.class)));

            verify(rideRepository).findById(rideId);
            verifyNoMoreInteractions(rideRepository, rideMapper);
        }

        @Test
        void updateRideStatusInvalid() throws Exception {
            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.PICKING_UP);
            testRide.setRideStatus(RideStatuses.CREATED);
            String expectedMessage = ExceptionMessages.INVALID_STATUS_TRANSITION.format(testRide.getRideStatus(), requestChangeStatus.newStatus());

            when(rideRepository.findById(rideId)).thenReturn(Optional.of(testRide));

            InvalidStatusTransitionException exception = assertThrows(InvalidStatusTransitionException.class, () -> {
                rideService.updateRideStatus(rideId, requestChangeStatus);
            });

            assertEquals(expectedMessage, exception.getMessage());
            verify(rideRepository).findById(rideId);
            verifyNoMoreInteractions(rideRepository, rideMapper, kafkaProducer);
        }
    }

    @Nested
    class GetRideTests {
        @Test
        void getRideByIdOk() {
            when(rideRepository.findById(rideId)).thenReturn(Optional.of(testRide));
            when(rideMapper.rideToResponseRide(any(Ride.class))).thenReturn(testResponseRide);

            ResponseRide result = rideService.getRideById(rideId);

            verify(rideRepository).findById(rideId);
            verify(rideMapper).rideToResponseRide(testRide);
            assertEquals(testResponseRide, result);
        }

        @Test
        void getRideByIdNotFound() {
            when(rideRepository.findById(rideId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(rideId)));

            assertThrows(EntityNotFoundException.class, () -> rideService.getRideById(rideId));

            verify(rideRepository).findById(rideId);
        }

        @Test
        void getAllRidesOk() {
            Pageable pageable = RideTestEntityUtils.createDefaultPageRequest();
            Page<Ride> ridePage = RideTestEntityUtils.createDefaultRidePage(List.of(testRide));
            when(rideRepository.findAll(pageable)).thenReturn(ridePage);
            when(rideMapper.rideToResponseRide(any(Ride.class))).thenReturn(testResponseRide);

            PagedResponseRideList result = rideService.getAllRides(pageable);

            verify(rideRepository).findAll(pageable);
            assertEquals(1, result.totalElements());
            assertEquals(testResponseRide, result.rides().get(0));
        }
    }

    @Nested
    class RideExistsTests {
        @Test
        void doesRideExistForDriverOk() {
            Long driverId = RideTestEntityUtils.DEFAULT_DRIVER_ID;
            when(rideRepository.existsByIdAndDriverId(rideId, driverId)).thenReturn(true);

            Boolean result = rideService.doesRideExistForDriver(rideId, driverId);

            verify(rideRepository).existsByIdAndDriverId(rideId, driverId);
            assertTrue(result);
        }

        @Test
        void doesRideExistForDriverNotFound() {
            Long driverId = RideTestEntityUtils.DEFAULT_DRIVER_ID;

            when(rideRepository.existsByIdAndDriverId(rideId, driverId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND_FOR_DRIVER.format(rideId, driverId)));

            assertThrows(EntityNotFoundException.class, () -> rideService.doesRideExistForDriver(rideId, driverId));

            verify(rideRepository).existsByIdAndDriverId(rideId, driverId);
        }

        @Test
        void doesRideExistForPassengerOk() {
            Long passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;
            when(rideRepository.existsByIdAndPassengerId(rideId, passengerId)).thenReturn(true);

            Boolean result = rideService.doesRideExistForPassenger(rideId, passengerId);

            verify(rideRepository).existsByIdAndPassengerId(rideId, passengerId);
            assertTrue(result);
        }

        @Test
        void doesRideExistForPassengerNotFound() {
            Long passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;

            when(rideRepository.existsByIdAndPassengerId(rideId, passengerId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND_FOR_PASSENGER.format(rideId, passengerId)));

            assertThrows(EntityNotFoundException.class, () -> rideService.doesRideExistForPassenger(rideId, passengerId));

            verify(rideRepository).existsByIdAndPassengerId(rideId, passengerId);
        }
    }

}