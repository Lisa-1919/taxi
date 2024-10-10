package com.example.rides_service.mapper;

import com.example.rides_service.dto.RequestRide;
import com.example.rides_service.dto.ResponseRide;
import com.example.rides_service.entity.Ride;
import com.example.rides_service.util.RideStatuses;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface RideMapper {

    Ride requestRideToRide(RequestRide requestRide);

    ResponseRide rideToResponseRide(Ride ride);

    @Mapping(target = "rideStatus", expression = "java(updateStatusBasedOnDriver(requestRide.driverId(), rideFromDB))")
    @Mapping(target = "driverId", expression = "java(updateDriver(requestRide.driverId(), rideFromDB))")
    void updateRideFromRideDto(RequestRide requestRide, @MappingTarget Ride rideFromDB);

    default Long updateDriver(Long newDriverId, Ride rideFromDB) {
        if (rideFromDB.getDriverId() == null) {
            return newDriverId;
        }
        return rideFromDB.getDriverId();
    }

    default RideStatuses updateStatusBasedOnDriver(Long newDriverId, Ride rideFromDB) {
        if (rideFromDB.getDriverId() == null && newDriverId != null) {
            return RideStatuses.ACCEPTED;
        }
        return rideFromDB.getRideStatus();
    }
}
