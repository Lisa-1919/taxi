package com.example.rides_service.service;

import com.example.rides_service.dto.RequestRide;
import com.example.rides_service.dto.ResponseRide;
import com.example.rides_service.dto.ResponseRideList;
import com.example.rides_service.util.RideStatuses;

public interface RideService {

    ResponseRide addRide(RequestRide requestRide);

    ResponseRide editRide(Long id, RequestRide requestRide);

    ResponseRide updateRideStatus(Long id, RideStatuses status);

    ResponseRide getRideById(Long id);
    ResponseRideList getAllRides();

}
