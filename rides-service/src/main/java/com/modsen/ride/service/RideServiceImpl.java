package com.modsen.ride.service;

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
import com.modsen.ride.util.ExceptionMessages;
import com.modsen.ride.util.RideStatuses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class RideServiceImpl implements RideService {

    private static final String RIDE_STATUS_UPDATE_MESSAGE = "The status of your ride with id %d changed to %s";

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final DriverServiceClient driverServiceClient;
    private final PassengerServiceClient passengerServiceClient;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public ResponseRide addRide(RequestRide requestRide) {

        doesPassengerExist(requestRide.passengerId());

        Ride ride = rideMapper.requestRideToRide(requestRide);
        ride.setOrderDateTime(LocalDateTime.now());
        ride.setRideStatus(RideStatuses.CREATED);

        return rideMapper.rideToResponseRide(rideRepository.save(ride));
    }

    @Override
    @Transactional
    public ResponseRide editRide(Long id, RequestRide requestRide) {

        doesDriverExist(requestRide.driverId());
        doesPassengerExist(requestRide.passengerId());

        Ride rideFromDB = getOrThrow(id);
        rideMapper.updateRideFromRideDto(requestRide, rideFromDB);
        ResponseRide responseRide = rideMapper.rideToResponseRide(rideRepository.save(rideFromDB));

        sendUpdateStatusMessageToPassenger(responseRide.id(), responseRide.rideStatus());

        return responseRide;
    }

    @Override
    @Transactional
    public ResponseRide updateRideStatus(Long id, RequestChangeStatus requestChangeStatus) {

        Ride rideFromDB = getOrThrow(id);

        RideStatuses currentStatus = rideFromDB.getRideStatus();

        try {
            RideStatuses updatedStatus = currentStatus.transition(requestChangeStatus.newStatus());
            rideFromDB.setRideStatus(updatedStatus);
        } catch (Exception e) {
            throw new InvalidStatusTransitionException(ExceptionMessages.INVALID_STATUS_TRANSITION.format(currentStatus, requestChangeStatus.newStatus()));
        }

        ResponseRide responseRide = rideMapper.rideToResponseRide(rideRepository.save(rideFromDB));

        sendUpdateStatusMessageToPassenger(responseRide.id(), responseRide.rideStatus());

        return responseRide;
    }

    @Override
    public ResponseRide getRideById(Long id) {
        Ride ride = getOrThrow(id);
        return rideMapper.rideToResponseRide(ride);
    }

    @Override
    public PagedResponseRideList getAllRides(int page, int limit) {
        Page<Ride> ridePage = rideRepository.findAll(PageRequest.of(page, limit));
        List<ResponseRide> responseRideList = ridePage
                .map(rideMapper::rideToResponseRide)
                .toList();
        return new PagedResponseRideList(
                responseRideList,
                ridePage.getNumber(),
                ridePage.getSize(),
                ridePage.getTotalElements(),
                ridePage.getTotalPages(),
                ridePage.isLast()
        );
    }

    @Override
    public Boolean doesRideExistForDriver(Long id, Long driverId) {
        boolean exists = rideRepository.existsByIdAndDriverId(id, driverId);
        if (exists) return true;
        else throw new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND_FOR_DRIVER.format(id, driverId));
    }

    @Override
    public Boolean doesRideExistForPassenger(Long id, Long passengerId) {
        boolean exists = rideRepository.existsByIdAndPassengerId(id, passengerId);
        if (exists) return true;
        else throw new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND_FOR_PASSENGER.format(id, passengerId));
    }

    private Ride getOrThrow(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(id)));
    }

    private void doesDriverExist(Long driverId) {
        driverServiceClient.doesDriverExists(driverId);
    }

    private void doesPassengerExist(Long passengerId) {
        passengerServiceClient.doesPassengerExists(passengerId);
    }

    private void sendUpdateStatusMessageToPassenger(Long rideId, RideStatuses rideStatus) {
        String message = String.format(RIDE_STATUS_UPDATE_MESSAGE, rideId, rideStatus.toString());
        kafkaProducer.send(new UpdateStatusMessage(message));
    }

}
