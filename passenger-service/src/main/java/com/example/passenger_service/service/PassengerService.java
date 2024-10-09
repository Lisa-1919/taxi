package com.example.passenger_service.service;

import com.example.passenger_service.dto.PassengerDto;

import java.util.List;

public interface PassengerService {

    PassengerDto addPassenger(PassengerDto passengerDto);

    PassengerDto editPassenger(Long id, PassengerDto updatedPassengerDto);

    void deletePassenger(Long id);

    PassengerDto getPassengerById(Long id);

    List<PassengerDto> getAllPassengers();

}
