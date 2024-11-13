package com.example.ride.mapper;

import com.example.ride.dto.RequestRide;
import com.example.ride.dto.ResponseRide;
import com.example.ride.entity.Ride;
import com.example.ride.util.RideStatuses;
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
