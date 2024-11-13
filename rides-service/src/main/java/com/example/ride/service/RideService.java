package com.example.ride.service;

import com.example.ride.dto.PagedResponseRideList;
import com.example.ride.dto.RequestChangeStatus;
import com.example.ride.dto.RequestRide;
import com.example.ride.dto.ResponseRide;

public interface RideService {

    ResponseRide addRide(RequestRide requestRide);

    ResponseRide editRide(Long id, RequestRide requestRide);

    ResponseRide updateRideStatus(Long id, RequestChangeStatus requestChangeStatus);

    ResponseRide getRideById(Long id);

    PagedResponseRideList getAllRides(int page, int limit);

    Boolean doesRideExistForDriver(Long id, Long driverId);

    Boolean doesRideExistForPassenger(Long id, Long passengerId);

}
