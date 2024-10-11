package com.example.rides_service.service;

import com.example.rides_service.dto.RequestRide;
import com.example.rides_service.dto.ResponseRide;
import com.example.rides_service.dto.ResponseRideList;
import com.example.rides_service.entity.Ride;
import com.example.rides_service.mapper.RideMapper;
import com.example.rides_service.repo.RideRepository;
import com.example.rides_service.util.ExceptionMessages;
import com.example.rides_service.util.RideStatuses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;

    private final RideMapper rideMapper;

    @Override
    @Transactional
    public ResponseRide addRide(RequestRide requestRide) {

        isPassengerExists(requestRide.passengerId());

        Ride ride = rideMapper.requestRideToRide(requestRide);
        ride.setOrderDateTime(LocalDateTime.now());
        ride.setRideStatus(RideStatuses.CREATED);

        return rideMapper.rideToResponseRide(rideRepository.save(ride));
    }

    @Override
    @Transactional
    public ResponseRide editRide(Long id, RequestRide requestRide) {

        isDriverExists(requestRide.driverId());

        Ride rideFromDB = getOrThrow(id);

        rideMapper.updateRideFromRideDto(requestRide, rideFromDB);

        return rideMapper.rideToResponseRide(rideRepository.save(rideFromDB));
    }

    @Override
    @Transactional
    public ResponseRide updateRideStatus(Long id, RideStatuses newStatus) {
        Ride rideFromDB = getOrThrow(id);

        RideStatuses currentStatus = rideFromDB.getRideStatus();

        try {

            RideStatuses updatedStatus = currentStatus.transition(newStatus);
            rideFromDB.setRideStatus(updatedStatus);

        } catch (Exception e) {

            throw new IllegalArgumentException(ExceptionMessages.INVALID_STATUS_TRANSITION.format(currentStatus, newStatus));
        }

        return rideMapper.rideToResponseRide(rideRepository.save(rideFromDB));
    }

    @Override
    public ResponseRide getRideById(Long id) {
        Ride ride = getOrThrow(id);
        return rideMapper.rideToResponseRide(ride);
    }

    @Override
    public ResponseRideList getAllRides() {
        return new ResponseRideList(rideRepository.findAll().stream().map(rideMapper::rideToResponseRide).toList());
    }

    private Ride getOrThrow(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(id)));
    }

    //TO DO: request to the driver service for the existence of a driver with this id
    private boolean isDriverExists(Long driverId) {
        return true;
    }

    //TO DO: request to the passenger service for the existence of a driver with this id
    private boolean isPassengerExists(Long passengerId) {
        return true;
    }
}
