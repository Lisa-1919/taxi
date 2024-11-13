package com.modsen.passenger.service;

import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.dto.PagedResponsePassengerList;
import org.springframework.data.domain.Pageable;

public interface PassengerService {

    ResponsePassenger addPassenger(RequestPassenger requestPassenger);

    ResponsePassenger editPassenger(Long id, RequestPassenger requestPassenger);

    void deletePassenger(Long id);

    ResponsePassenger getPassengerById(Long id);

    ResponsePassenger getPassengerByIdNonDeleted(Long id);

    PagedResponsePassengerList getAllPassengers(Pageable pageable);

    PagedResponsePassengerList getAllNonDeletedPassengers(Pageable pageable);

    boolean doesPassengerExist(Long id);
}
