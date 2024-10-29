package com.example.rides_service.service;

import com.example.rides_service.dto.PagedResponseRideList;
import com.example.rides_service.dto.RequestChangeStatus;
import com.example.rides_service.dto.RequestRide;
import com.example.rides_service.dto.ResponseRide;
import org.springframework.data.domain.Pageable;

public interface RideService {

    ResponseRide addRide(RequestRide requestRide);
    ResponseRide editRide(Long id, RequestRide requestRide);
    ResponseRide updateRideStatus(Long id, RequestChangeStatus requestChangeStatus);
    ResponseRide getRideById(Long id);
    PagedResponseRideList getAllRides(Pageable pageable);
    Boolean doesRideExist(Long id);

}
