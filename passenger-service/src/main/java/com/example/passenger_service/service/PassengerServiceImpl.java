package com.example.passenger_service.service;

import com.example.passenger_service.dto.PassengerDto;
import com.example.passenger_service.entity.Passenger;
import com.example.passenger_service.mapper.PassengerMapper;
import com.example.passenger_service.repo.PassengerRepository;
import com.example.passenger_service.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    public PassengerDto addPassenger(PassengerDto passengerDto) {
        try {
            Passenger passenger = passengerMapper.passengerDtoToPassenger(passengerDto);
            passengerDto = passengerMapper.passengerToPassengerDto(passengerRepository.save(passenger));
            return passengerDto;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public PassengerDto editPassenger(Long id, PassengerDto updatedPassengerDto) {
        Passenger passengerFromDb = passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));

        passengerMapper.updatePassengerFromPassengerDto(updatedPassengerDto, passengerFromDb);

        try {
            return passengerMapper.passengerToPassengerDto(passengerRepository.save(passengerFromDb));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_PASSENGER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));

        passengerRepository.delete(passenger);
    }

    @Override
    public PassengerDto getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));

        return passengerMapper.passengerToPassengerDto(passenger);
    }

    @Override
    public List<PassengerDto> getAllPassengers() {
        return passengerRepository.findAll().stream().map(passengerMapper::passengerToPassengerDto).toList();
    }
}
