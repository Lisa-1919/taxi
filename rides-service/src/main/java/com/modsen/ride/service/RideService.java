package com.modsen.ride.service;

import com.modsen.ride.dto.PagedResponseRideList;
import com.modsen.ride.dto.RequestChangeStatus;
import com.modsen.ride.dto.RequestRide;
import com.modsen.ride.dto.ResponseRide;

public interface RideService {

    ResponseRide addRide(RequestRide requestRide);

    ResponseRide editRide(Long id, RequestRide requestRide);

    ResponseRide updateRideStatus(Long id, RequestChangeStatus requestChangeStatus);

    ResponseRide getRideById(Long id);

    PagedResponseRideList getAllRides(int page, int limit);

    Boolean doesRideExistForDriver(Long id, Long driverId);

    Boolean doesRideExistForPassenger(Long id, Long passengerId);

}
