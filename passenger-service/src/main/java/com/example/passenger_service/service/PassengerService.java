package com.example.passenger_service.service;

import com.example.passenger_service.dto.RequestPassenger;
import com.example.passenger_service.dto.ResponsePassenger;
import com.example.passenger_service.dto.ResponsePassengerList;

import java.util.List;

public interface PassengerService {

    ResponsePassenger addPassenger(RequestPassenger requestPassenger);

    ResponsePassenger editPassenger(Long id, RequestPassenger requestPassenger);

    void deletePassenger(Long id);

    ResponsePassenger getPassengerById(Long id);

    ResponsePassengerList getAllPassengers();

}
