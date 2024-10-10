package com.example.rides_service.service;

import com.example.rides_service.dto.RideDto;
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
import java.util.List;

@RequiredArgsConstructor
@Service
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;

    private final RideMapper rideMapper;

    @Override
    @Transactional
    public RideDto addRide(RideDto rideDto) {

        isPassengerExists(rideDto.passengerId());

        Ride ride = rideMapper.rideFromRideDto(rideDto);
        ride.setOrderDateTime(LocalDateTime.now());
        ride.setRideStatus(RideStatuses.CREATED);

        return rideMapper.rideDtoFromRide(rideRepository.save(ride));
    }

    @Override
    @Transactional
    public RideDto editRide(Long id, RideDto rideDto) {

        isDriverExists(rideDto.driverId());

        Ride rideFromDB = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(id)));

        rideMapper.updateRideFromRideDto(rideDto, rideFromDB);

        return rideMapper.rideDtoFromRide(rideRepository.save(rideFromDB));
    }

    @Override
    @Transactional
    public RideDto updateRideStatus(Long id, RideStatuses newStatus) {
        Ride rideFromDB = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(id)));

        RideStatuses currentStatus = rideFromDB.getRideStatus();

        try {

            RideStatuses updatedStatus = currentStatus.transition(newStatus);
            rideFromDB.setRideStatus(updatedStatus);

        } catch (Exception e) {

            throw new IllegalArgumentException(ExceptionMessages.INVALID_STATUS_TRANSITION.format(currentStatus, newStatus));
        }

        return rideMapper.rideDtoFromRide(rideRepository.save(rideFromDB));
    }

    @Override
    public RideDto getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(id)));

        return rideMapper.rideDtoFromRide(ride);
    }

    @Override
    public List<RideDto> getAllRides() {
        return rideRepository.findAll().stream().map(rideMapper::rideDtoFromRide).toList();
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
