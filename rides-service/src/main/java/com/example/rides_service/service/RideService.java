package com.example.rides_service.service;

import com.example.rides_service.dto.RideDto;
import com.example.rides_service.util.RideStatuses;

import java.util.List;

public interface RideService {

    RideDto addRide(RideDto rideDto);

    RideDto editRide(Long id, RideDto rideDto);

    RideDto updateRideStatus(Long id, RideStatuses status);

    RideDto getRideById(Long id);

    List<RideDto> getAllRides();

}
