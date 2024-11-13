package com.modsen.ride.unit.service;

import com.modsen.ride.client.DriverServiceClient;
import com.modsen.ride.client.PassengerServiceClient;
import com.modsen.ride.dto.PagedResponseRideList;
import com.modsen.ride.dto.RequestChangeStatus;
import com.modsen.ride.dto.RequestRide;
import com.modsen.ride.dto.ResponseRide;
import com.modsen.ride.dto.UpdateStatusMessage;
import com.modsen.ride.entity.Ride;
import com.modsen.ride.exception.InvalidStatusTransitionException;
import com.modsen.ride.mapper.RideMapper;
import com.modsen.ride.repo.RideRepository;
import com.modsen.ride.service.KafkaProducer;
import com.modsen.ride.service.RideServiceImpl;
import com.modsen.ride.util.ExceptionMessages;
import com.modsen.ride.util.RideStatuses;
import com.modsen.ride.util.RideTestEntityUtils;
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

    private static final String RIDE_STATUS_UPDATE_MESSAGE = "The status of your ride with id %d changed to %s";

    @BeforeEach
    void setUp() {
        rideId = RideTestEntityUtils.DEFAULT_RIDE_ID;
        testRide = RideTestEntityUtils.createTestRide().build();
        testRequestRide = RideTestEntityUtils.createTestRequestRide().build();
        testResponseRide = RideTestEntityUtils.createTestResponseRide().build();
    }

    @Nested
    class AddRideTests {
        @Test
        void addRideOk() {
            when(rideMapper.requestRideToRide(testRequestRide))
                    .thenReturn(testRide);
            when(rideRepository.save(testRide))
                    .thenReturn(testRide);
            when(rideMapper.rideToResponseRide(testRide))
                    .thenReturn(testResponseRide);

            ResponseRide result = rideService.addRide(testRequestRide);

            verify(passengerServiceClient).doesPassengerExists(testRequestRide.passengerId());
            verify(rideRepository).save(testRide);
            verify(rideMapper).rideToResponseRide(testRide);
            assertEquals(testResponseRide, result);
        }

        @Test
        void addRidePassengerNotFound() {
            doThrow(EntityNotFoundException.class)
                    .when(passengerServiceClient)
                    .doesPassengerExists(testRequestRide.passengerId());

            assertThrows(EntityNotFoundException.class, () -> rideService.addRide(testRequestRide));

            verify(passengerServiceClient).doesPassengerExists(testRequestRide.passengerId());

            verifyNoMoreInteractions(rideRepository, rideMapper);
        }
    }

    @Nested
    class EditRideTests {
        @Test
        void editRideOk() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide()
                    .driverId(RideTestEntityUtils.DEFAULT_DRIVER_ID)
                    .build();
            Ride updatedRide = RideTestEntityUtils.createTestRide()
                    .driverId(RideTestEntityUtils.DEFAULT_DRIVER_ID)
                    .rideStatus(RideStatuses.ACCEPTED)
                    .build();
            ResponseRide responseRide = RideTestEntityUtils.createTestResponseRide()
                    .rideStatus(RideStatuses.ACCEPTED)
                    .build();
            String message = String.format(RIDE_STATUS_UPDATE_MESSAGE, rideId, updatedRide.getRideStatus().toString());
            UpdateStatusMessage updateStatusMessage = new UpdateStatusMessage(message);

            when(rideRepository.findById(rideId))
                    .thenReturn(Optional.of(testRide));
            when(rideRepository.save(testRide))
                    .thenReturn(updatedRide);
            when(rideMapper.rideToResponseRide(updatedRide))
                    .thenReturn(responseRide);

            ResponseRide result = rideService.editRide(rideId, requestRide);

            assertEquals(responseRide, result);

            verify(driverServiceClient).doesDriverExists(requestRide.driverId());
            verify(passengerServiceClient).doesPassengerExists(requestRide.passengerId());
            verify(rideMapper).updateRideFromRideDto(requestRide, testRide);
            verify(rideRepository).save(testRide);
            verify(kafkaProducer).send(updateStatusMessage);
        }

        @Test
        void editRidePassengerNotFound() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide()
                    .driverId(RideTestEntityUtils.DEFAULT_DRIVER_ID)
                    .build();

            doThrow(EntityNotFoundException.class)
                    .when(passengerServiceClient)
                    .doesPassengerExists(requestRide.passengerId());

            assertThrows(EntityNotFoundException.class, () -> rideService.editRide(rideId, requestRide));

            verify(driverServiceClient).doesDriverExists(requestRide.driverId());
            verify(passengerServiceClient).doesPassengerExists(requestRide.passengerId());

            verifyNoMoreInteractions(rideRepository, rideMapper);
        }

        @Test
        void editRideDriverNotFound() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide()
                    .driverId(RideTestEntityUtils.DEFAULT_DRIVER_ID)
                    .build();

            doThrow(EntityNotFoundException.class)
                    .when(driverServiceClient)
                    .doesDriverExists(requestRide.driverId());

            assertThrows(EntityNotFoundException.class, () -> rideService.editRide(rideId, requestRide));

            verify(driverServiceClient).doesDriverExists(requestRide.driverId());

            verifyNoMoreInteractions(rideRepository, rideMapper);
        }

        @Test
        void editRideNotFound() {
            RequestRide requestRide = RideTestEntityUtils.createTestRequestRide()
                    .driverId(RideTestEntityUtils.DEFAULT_DRIVER_ID)
                    .build();

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

            when(rideRepository.findById(rideId))
                    .thenReturn(Optional.of(testRide));
            when(rideRepository.save(testRide))
                    .thenReturn(testRide);
            when(rideMapper.rideToResponseRide(testRide))
                    .thenReturn(testResponseRide);

            ResponseRide result = rideService.updateRideStatus(rideId, requestChangeStatus);

            assertEquals(testResponseRide, result);

            verify(rideRepository).save(testRide);
        }

        @Test
        void updateRideStatusRideNotFound() {
            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.ACCEPTED);

            when(rideRepository.findById(rideId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(rideId)));

            assertThrows(EntityNotFoundException.class, () -> rideService.updateRideStatus(rideId, requestChangeStatus));

            verify(rideRepository).findById(rideId);
            verifyNoMoreInteractions(rideRepository, rideMapper);
        }

        @Test
        void updateRideStatusInvalid() throws Exception {
            RequestChangeStatus requestChangeStatus = RideTestEntityUtils.createChangeStatusRequest(RideStatuses.PICKING_UP);
            testRide.setRideStatus(RideStatuses.CREATED);
            String expectedMessage = ExceptionMessages.INVALID_STATUS_TRANSITION.format(testRide.getRideStatus(), requestChangeStatus.newStatus());

            when(rideRepository.findById(rideId))
                    .thenReturn(Optional.of(testRide));

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
            when(rideRepository.findById(rideId))
                    .thenReturn(Optional.of(testRide));
            when(rideMapper.rideToResponseRide(testRide))
                    .thenReturn(testResponseRide);

            ResponseRide result = rideService.getRideById(rideId);

            assertEquals(testResponseRide, result);

            verify(rideRepository).findById(rideId);
            verify(rideMapper).rideToResponseRide(testRide);
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

            when(rideRepository.findAll(pageable))
                    .thenReturn(ridePage);
            when(rideMapper.rideToResponseRide(testRide))
                    .thenReturn(testResponseRide);

            PagedResponseRideList result = rideService.getAllRides(pageable.getPageNumber(), pageable.getPageSize());

            assertEquals(RideTestEntityUtils.DEFAULT_TOTAL_ELEMENTS, result.totalElements());
            assertEquals(testResponseRide, result.rides().get(0));

            verify(rideRepository).findAll(pageable);
        }
    }

    @Nested
    class RideExistsTests {
        @Test
        void doesRideExistForDriverOk() {
            Long driverId = RideTestEntityUtils.DEFAULT_DRIVER_ID;

            when(rideRepository.existsByIdAndDriverId(rideId, driverId))
                    .thenReturn(true);

            Boolean result = rideService.doesRideExistForDriver(rideId, driverId);

            assertTrue(result);

            verify(rideRepository).existsByIdAndDriverId(rideId, driverId);
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

            when(rideRepository.existsByIdAndPassengerId(rideId, passengerId))
                    .thenReturn(true);

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