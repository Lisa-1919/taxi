package com.example.passenger_service.mapper;

import com.example.passenger_service.dto.RequestPassenger;
import com.example.passenger_service.dto.ResponsePassenger;
import com.example.passenger_service.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface PassengerMapper {

    Passenger requestPassengerToPassenger(RequestPassenger requestPassenger);

    ResponsePassenger passengerToResponsePassenger(Passenger passenger);

    void updatePassengerFromRequestPassenger(RequestPassenger requestPassenger, @MappingTarget Passenger passenger);
}
