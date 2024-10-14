package com.example.passenger_service.service;

import com.example.passenger_service.dto.RequestPassenger;
import com.example.passenger_service.dto.ResponsePassenger;
import com.example.passenger_service.dto.PagedResponsePassengerList;
import com.example.passenger_service.entity.Passenger;
import com.example.passenger_service.mapper.PassengerMapper;
import com.example.passenger_service.repo.PassengerRepository;
import com.example.passenger_service.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            throw new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format());
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
            throw new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format());
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
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));

        return passengerMapper.passengerToResponsePassenger(passenger);
    }

    @Override
    public ResponsePassenger getPassengerByIdNonDeleted(Long id) {
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
        Page<Passenger> passengerPage = passengerRepository.getAllNonDeleted(pageable);
        return getPagedResponsePassengerListFromPage(passengerPage);
    }

    private Passenger getOrThrow(Long id) {
        return passengerRepository.getPassengerByIdNonDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));
    }

    private PagedResponsePassengerList getPagedResponsePassengerListFromPage(Page<Passenger> passengerPage) {
        List<ResponsePassenger> responsePassengerList = passengerPage.stream()
                .map(passengerMapper::passengerToResponsePassenger).toList();

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
