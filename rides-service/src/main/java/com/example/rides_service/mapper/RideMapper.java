package com.example.rides_service.mapper;

import com.example.rides_service.dto.RideDto;
import com.example.rides_service.entity.Ride;
import com.example.rides_service.util.RideStatuses;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface RideMapper {

    Ride rideFromRideDto(RideDto rideDto);

    RideDto rideDtoFromRide(Ride ride);

    @Mapping(target = "rideStatus", expression = "java(updateStatusBasedOnDriver(rideDto.driverId(), rideFromDB))")
    @Mapping(target = "driverId", expression = "java(updateDriver(rideDto.driverId(), rideFromDB))")
    void updateRideFromRideDto(RideDto rideDto, @MappingTarget Ride rideFromDB);

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
