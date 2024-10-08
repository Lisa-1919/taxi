package com.example.passenger_service.mapper;

import com.example.passenger_service.dto.PassengerDto;
import com.example.passenger_service.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface PassengerMapper {

    Passenger PassengerDtoToPassenger(PassengerDto passengerDto);

    PassengerDto PassengerToPassengerDto(Passenger passenger);

    void updatePassengerFromPassengerDto(PassengerDto passengerDto, @MappingTarget Passenger passenger);
}
