package com.example.passenger_service.service;

import com.example.passenger_service.dto.RequestPassenger;
import com.example.passenger_service.dto.ResponsePassenger;
import com.example.passenger_service.dto.PagedResponsePassengerList;
import org.springframework.data.domain.Pageable;

public interface PassengerService {

    ResponsePassenger addPassenger(RequestPassenger requestPassenger);

    ResponsePassenger editPassenger(Long id, RequestPassenger requestPassenger);

    void deletePassenger(Long id);

    ResponsePassenger getPassengerById(Long id);

    ResponsePassenger getPassengerByIdNonDeleted(Long id);

    PagedResponsePassengerList getAllPassengers(Pageable pageable);

    PagedResponsePassengerList getAllNonDeletedPassengers(Pageable pageable);

    boolean passengerExists(Long id);
}
