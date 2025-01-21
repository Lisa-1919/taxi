package com.modsen.passenger.service;

import com.modsen.passenger.dto.CreatePassengerRequest;
import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.dto.PagedResponsePassengerList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PassengerService {

    ResponsePassenger addPassenger(CreatePassengerRequest createPassengerRequest);

    ResponsePassenger editPassenger(UUID id, RequestPassenger requestPassenger);

    void deletePassenger(UUID id);

    ResponsePassenger getPassengerById(UUID id);

    ResponsePassenger getPassengerByIdNonDeleted(UUID id);

    PagedResponsePassengerList getAllPassengers(Pageable pageable);

    PagedResponsePassengerList getAllNonDeletedPassengers(Pageable pageable);

    boolean doesPassengerExist(UUID id);
}
