package com.example.passenger_service.service;

import com.example.passenger_service.dto.PagedResponsePassengerList;
import com.example.passenger_service.dto.RequestPassenger;
import com.example.passenger_service.dto.ResponsePassenger;
import com.example.passenger_service.entity.Passenger;
import com.example.passenger_service.mapper.PassengerMapper;
import com.example.passenger_service.repo.PassengerRepository;
import com.example.passenger_service.util.ExceptionMessages;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private static final String EMAIL = "email";
    private static final String PHONE_NUMBER = "phoneNumber";

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    @Transactional
    @CircuitBreaker(name = "passengerService", fallbackMethod = "fallbackPassengerResponse")
    public ResponsePassenger addPassenger(RequestPassenger requestPassenger) {

        checkUniqueField(EMAIL, requestPassenger.email(), passengerRepository::existsByEmail);
        checkUniqueField(PHONE_NUMBER, requestPassenger.phoneNumber(), passengerRepository::existsByPhoneNumber);

        Passenger passenger = passengerMapper.requestPassengerToPassenger(requestPassenger);
        return passengerMapper.passengerToResponsePassenger(passengerRepository.save(passenger));

    }

    @Override
    @Transactional
    @CircuitBreaker(name = "passengerService", fallbackMethod = "fallbackPassengerResponse")
    public ResponsePassenger editPassenger(Long id, RequestPassenger requestPassenger) {
        Passenger passengerFromDb = getOrThrow(id);

        if (!passengerFromDb.getEmail().equals(requestPassenger.email())){
            checkUniqueField(EMAIL, requestPassenger.email(), passengerRepository::existsByEmail);
        }

        if (!passengerFromDb.getPhoneNumber().equals(requestPassenger.phoneNumber())){
            checkUniqueField(PHONE_NUMBER, requestPassenger.phoneNumber(), passengerRepository::existsByPhoneNumber);
        }

        passengerMapper.updatePassengerFromRequestPassenger(requestPassenger, passengerFromDb);

        return passengerMapper.passengerToResponsePassenger(passengerRepository.save(passengerFromDb));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "passengerService", fallbackMethod = "fallbackVoidResponse")
    public void deletePassenger(Long id) {
        Passenger passenger = getOrThrow(id);
        passengerRepository.delete(passenger);
    }

    @Override
    @CircuitBreaker(name = "passengerService", fallbackMethod = "fallbackPassengerResponse")
    public ResponsePassenger getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));

        return passengerMapper.passengerToResponsePassenger(passenger);
    }

    @Override
    @CircuitBreaker(name = "passengerService", fallbackMethod = "fallbackPassengerResponse")
    public ResponsePassenger getPassengerByIdNonDeleted(Long id) {
        Passenger passenger = getOrThrow(id);
        return passengerMapper.passengerToResponsePassenger(passenger);
    }

    @Override
    @CircuitBreaker(name = "passengerService", fallbackMethod = "fallbackPagedResponse")
    public PagedResponsePassengerList getAllPassengers(Pageable pageable) {
        Page<Passenger> passengerPage = passengerRepository.findAll(pageable);
        return getPagedResponsePassengerListFromPage(passengerPage);
    }

    @Override
    @CircuitBreaker(name = "passengerService", fallbackMethod = "fallbackPagedResponse")
    public PagedResponsePassengerList getAllNonDeletedPassengers(Pageable pageable) {
        Page<Passenger> passengerPage = passengerRepository.findAllNonDeleted(pageable);
        return getPagedResponsePassengerListFromPage(passengerPage);
    }

    @Override
    @CircuitBreaker(name = "passengerService", fallbackMethod = "fallbackBooleanResponse")
    public boolean doesPassengerExist(Long id) {
        boolean exists = passengerRepository.existsByIdAndIsDeletedFalse(id);
        if(exists) return true;
        else throw new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id));
    }

    private Passenger getOrThrow(Long id) {
        return passengerRepository.findPassengerByIdNonDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));
    }

    private <T> void checkUniqueField(String fieldName, T fieldValue, Predicate<T> existsFunction) {
        if (existsFunction.test(fieldValue)) {
            throw new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format(fieldName, fieldValue));
        }
    }

    private PagedResponsePassengerList getPagedResponsePassengerListFromPage(Page<Passenger> passengerPage) {
        List<ResponsePassenger> responsePassengerList = passengerPage
                .map(passengerMapper::passengerToResponsePassenger)
                .toList();

        return new PagedResponsePassengerList(
                responsePassengerList,
                passengerPage.getNumber(),
                passengerPage.getSize(),
                passengerPage.getTotalElements(),
                passengerPage.getTotalPages(),
                passengerPage.isLast()
        );
    }

    public ResponsePassenger fallbackPassengerResponse(Long id, Throwable t) {
        return new ResponsePassenger(0L, null, null, null, null, null);
    }

    public PagedResponsePassengerList fallbackPagedResponse(Pageable pageable, Throwable t) {
        return new PagedResponsePassengerList(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0, true);
    }

    public void fallbackVoidResponse(Long id, Throwable t) {
        log.error("Failed to process request for driver id: {}. Error: {}", id, t.getMessage(), t);
    }

    public boolean fallbackBooleanResponse(Long id, Throwable t) {
        return false;
    }

}
