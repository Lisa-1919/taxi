package com.modsen.passenger.service;

import com.modsen.passenger.dto.CreatePassengerRequest;
import com.modsen.passenger.dto.PagedResponsePassengerList;
import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.entity.Passenger;
import com.modsen.passenger.mapper.PassengerMapper;
import com.modsen.passenger.repo.PassengerRepository;
import com.modsen.passenger.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
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
    public ResponsePassenger addPassenger(CreatePassengerRequest createPassengerRequest) {

        checkUniqueField(EMAIL, createPassengerRequest.email(), passengerRepository::existsByEmail);
        checkUniqueField(PHONE_NUMBER, createPassengerRequest.phoneNumber(), passengerRepository::existsByPhoneNumber);

        Passenger passenger = passengerMapper.createPassengerRequestToPassenger(createPassengerRequest);
        return passengerMapper.passengerToResponsePassenger(passengerRepository.save(passenger));

    }

    @Override
    @Transactional
    public ResponsePassenger editPassenger(UUID id, RequestPassenger requestPassenger) {
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
    public void deletePassenger(UUID id) {
        Passenger passenger = getOrThrow(id);
        passengerRepository.delete(passenger);
    }

    @Override
    public ResponsePassenger getPassengerById(UUID id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));

        return passengerMapper.passengerToResponsePassenger(passenger);
    }

    @Override
    public ResponsePassenger getPassengerByIdNonDeleted(UUID id) {
        Passenger passenger = getOrThrow(id);
        return passengerMapper.passengerToResponsePassenger(passenger);
    }

    @Override
    public PagedResponsePassengerList getAllPassengers(Pageable pageable) {
        Page<Passenger> passengerPage = passengerRepository.findAll(pageable);
        return getPagedResponsePassengerListFromPage(passengerPage);
    }

    @Override
    public PagedResponsePassengerList getAllNonDeletedPassengers(Pageable pageable) {
        Page<Passenger> passengerPage = passengerRepository.findAllNonDeleted(pageable);
        return getPagedResponsePassengerListFromPage(passengerPage);
    }

    @Override
    public boolean doesPassengerExist(UUID id) {
        boolean exists = passengerRepository.existsByIdAndIsDeletedFalse(id);
        if(exists) return true;
        else throw new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id));
    }

    private Passenger getOrThrow(UUID id) {
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

}
