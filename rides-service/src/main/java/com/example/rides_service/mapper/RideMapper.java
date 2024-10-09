package com.example.rides_service.mapper;

import com.example.rides_service.dto.RideDto;
import com.example.rides_service.entity.Ride;
import org.mapstruct.Mapper;

@Mapper
public interface RideMapper {

    Ride rideFromRideDto(RideDto rideDto);

    RideDto rideDtoFromRide(Ride ride);

}
