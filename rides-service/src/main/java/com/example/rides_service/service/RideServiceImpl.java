package com.example.rides_service.service;

import com.example.rides_service.client.DriverServiceClient;
import com.example.rides_service.client.PassengerServiceClient;
import com.example.rides_service.dto.PagedResponseRideList;
import com.example.rides_service.dto.RequestChangeStatus;
import com.example.rides_service.dto.RequestRide;
import com.example.rides_service.dto.ResponseRide;
import com.example.rides_service.entity.Ride;
import com.example.rides_service.exception.InvalidStatusTransitionException;
import com.example.rides_service.mapper.RideMapper;
import com.example.rides_service.repo.RideRepository;
import com.example.rides_service.util.ExceptionMessages;
import com.example.rides_service.util.RideStatuses;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final DriverServiceClient driverServiceClient;
    private final PassengerServiceClient passengerServiceClient;

    @Override
    @Transactional
    @CircuitBreaker(name = "ridesService", fallbackMethod = "fallbackRideResponse")
    public ResponseRide addRide(RequestRide requestRide) {

        doesPassengerExist(requestRide.passengerId());

        Ride ride = rideMapper.requestRideToRide(requestRide);
        ride.setOrderDateTime(LocalDateTime.now());
        ride.setRideStatus(RideStatuses.CREATED);

        return rideMapper.rideToResponseRide(rideRepository.save(ride));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "ridesService", fallbackMethod = "fallbackRideResponse")
    public ResponseRide editRide(Long id, RequestRide requestRide) {

        doesDriverExist(requestRide.driverId());
        doesDriverExist(requestRide.passengerId());

        Ride rideFromDB = getOrThrow(id);

        rideMapper.updateRideFromRideDto(requestRide, rideFromDB);

        return rideMapper.rideToResponseRide(rideRepository.save(rideFromDB));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "ridesService", fallbackMethod = "fallbackRideResponse")
    public ResponseRide updateRideStatus(Long id, RequestChangeStatus requestChangeStatus) {

        Ride rideFromDB = getOrThrow(id);

        RideStatuses currentStatus = rideFromDB.getRideStatus();

        try {
            RideStatuses updatedStatus = currentStatus.transition(requestChangeStatus.newStatus());
            rideFromDB.setRideStatus(updatedStatus);
        } catch (Exception e) {
            throw new InvalidStatusTransitionException(ExceptionMessages.INVALID_STATUS_TRANSITION.format(currentStatus, requestChangeStatus.newStatus()));
        }

        return rideMapper.rideToResponseRide(rideRepository.save(rideFromDB));
    }

    @Override
    @CircuitBreaker(name = "ridesService", fallbackMethod = "fallbackRideResponse")
    public ResponseRide getRideById(Long id) {
        Ride ride = getOrThrow(id);
        return rideMapper.rideToResponseRide(ride);
    }

    @Override
    @CircuitBreaker(name = "ridesService", fallbackMethod = "fallbackPagedResponse")
    public PagedResponseRideList getAllRides(Pageable pageable) {
        Page<Ride> ridePage = rideRepository.findAll(pageable);
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
    @CircuitBreaker(name = "ridesService", fallbackMethod = "fallbackBooleanResponse")
    public Boolean doesRideExist(Long id) {
        boolean exists = rideRepository.existsById(id);
        if(exists) return true;
        else throw new EntityNotFoundException(ExceptionMessages.RIDE_NOT_FOUND.format(id));
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

    public ResponseRide fallbackDriverResponse(Long id, Throwable t) {
        return new ResponseRide(0L, 0L, 0L, null, null, null, null, BigDecimal.ZERO);
    }

    public PagedResponseRideList fallbackPagedResponse(Pageable pageable, Throwable t) {
        return new PagedResponseRideList(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0, true);
    }

    public void fallbackVoidResponse(Long id, Throwable t) {
        log.error("Failed to process request for ride id: {}. Error: {}", id, t.getMessage(), t);
    }

    public boolean fallbackBooleanResponse(Long id, Throwable t) {
        return false;
    }

}
