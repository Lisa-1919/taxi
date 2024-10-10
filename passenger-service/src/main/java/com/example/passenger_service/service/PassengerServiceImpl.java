package com.example.passenger_service.service;

import com.example.passenger_service.dto.RequestPassenger;
import com.example.passenger_service.dto.ResponsePassenger;
import com.example.passenger_service.dto.ResponsePassengerList;
import com.example.passenger_service.entity.Passenger;
import com.example.passenger_service.mapper.PassengerMapper;
import com.example.passenger_service.repo.PassengerRepository;
import com.example.passenger_service.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;


    @Override
    @Transactional
    public ResponsePassenger addPassenger(RequestPassenger requestPassenger) {
        try {
            Passenger passenger = passengerMapper.requestPassengerToPassenger(requestPassenger);
            return passengerMapper.passengerToResponsePassenger(passengerRepository.save(passenger));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public ResponsePassenger editPassenger(Long id, RequestPassenger requestPassenger) {
        Passenger passengerFromDb = getOrThrow(id);
        passengerMapper.updatePassengerFromRequestPassenger(requestPassenger, passengerFromDb);

        try {
            return passengerMapper.passengerToResponsePassenger(passengerRepository.save(passengerFromDb));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public void deletePassenger(Long id) {
        Passenger passenger = getOrThrow(id);
        passengerRepository.delete(passenger);
    }

    @Override
    public ResponsePassenger getPassengerById(Long id) {
        Passenger passenger = getOrThrow(id);
        return passengerMapper.passengerToResponsePassenger(passenger);
    }

    @Override
    public ResponsePassengerList getAllPassengers() {
        return new ResponsePassengerList(passengerRepository.findAll().stream().map(passengerMapper::passengerToResponsePassenger).toList());
    }

    private Passenger getOrThrow(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));
    }
}
