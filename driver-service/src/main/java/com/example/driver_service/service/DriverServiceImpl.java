package com.example.driver_service.service;

import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.dto.ResponseDriverList;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.DriverMapper;
import com.example.driver_service.repo.DriverRepository;
import com.example.driver_service.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    @Transactional
    public ResponseDriver addDriver(RequestDriver requestDriver) {
        try {
            Driver driver = driverMapper.requestDriverToDriver(requestDriver);
            return driverMapper.driverToResponseDriver(driverRepository.save(driver));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public ResponseDriver editDriver(Long id, RequestDriver requestDriver) {
        Driver driverFromDB = getOrThrow(id);
        driverMapper.updateDriverFromRequestDriver(requestDriver, driverFromDB);

        try {
            return driverMapper.driverToResponseDriver(driverRepository.save(driverFromDB));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = getOrThrow(id);
        driverRepository.delete(driver);
    }

    @Override
    public ResponseDriver getDriverById(Long id) {
        Driver driver = getOrThrow(id);
        if (driver.getCar() != null) {
            Hibernate.initialize(driver.getCar());
        }

        return driverMapper.driverToResponseDriver(driver);
    }

    @Override
    public ResponseDriverList getAllDrivers() {
        return new ResponseDriverList (driverRepository.findAll().stream().map(driverMapper::driverToResponseDriver).toList());
    }

    private Driver getOrThrow(Long id){
        return driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));
    }
}
