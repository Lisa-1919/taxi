package com.example.rides_service.service;

import com.example.rides_service.dto.RequestRide;
import com.example.rides_service.dto.ResponseRide;
import com.example.rides_service.dto.PagedResponseRideList;
import com.example.rides_service.util.RideStatuses;
import org.springframework.data.domain.Pageable;

public interface RideService {

    ResponseRide addRide(RequestRide requestRide);

    ResponseRide editRide(Long id, RequestRide requestRide);

    ResponseRide updateRideStatus(Long id, RideStatuses status);

    ResponseRide getRideById(Long id);
    PagedResponseRideList getAllRides(Pageable pageable);

}
