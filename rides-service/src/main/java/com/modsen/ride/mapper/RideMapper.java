package com.modsen.ride.mapper;

import com.modsen.ride.dto.RequestRide;
import com.modsen.ride.dto.ResponseRide;
import com.modsen.ride.entity.Ride;
import com.modsen.ride.util.RideStatuses;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper
public interface RideMapper {

    Ride requestRideToRide(RequestRide requestRide);

    ResponseRide rideToResponseRide(Ride ride);

    @Mapping(target = "rideStatus", expression = "java(updateStatusBasedOnDriver(requestRide.driverId(), rideFromDB))")
    @Mapping(target = "driverId", expression = "java(updateDriver(requestRide.driverId(), rideFromDB))")
    void updateRideFromRideDto(RequestRide requestRide, @MappingTarget Ride rideFromDB);

    default UUID updateDriver(UUID newDriverId, Ride rideFromDB) {
        if (rideFromDB.getDriverId() == null) {
            return newDriverId;
        }
        return rideFromDB.getDriverId();
    }

    default RideStatuses updateStatusBasedOnDriver(UUID newDriverId, Ride rideFromDB) {
        if (rideFromDB.getDriverId() == null && newDriverId != null) {
            return RideStatuses.ACCEPTED;
        }
        return rideFromDB.getRideStatus();
    }
}
