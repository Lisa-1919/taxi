package com.modsen.passenger.mapper;

import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface PassengerMapper {

    Passenger requestPassengerToPassenger(RequestPassenger requestPassenger);

    ResponsePassenger passengerToResponsePassenger(Passenger passenger);

    void updatePassengerFromRequestPassenger(RequestPassenger requestPassenger, @MappingTarget Passenger passenger);
}
