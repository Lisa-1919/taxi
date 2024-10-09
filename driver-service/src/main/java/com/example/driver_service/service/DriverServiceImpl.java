package com.example.driver_service.service;

import com.example.driver_service.dto.DriverDto;
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
    public DriverDto addDriver(DriverDto driverDto) {
        try {
            Driver driver = driverMapper.driverDtoToDriver(driverDto);
            driverDto = driverMapper.driverToDriverDto(driverRepository.save(driver));
            return driverDto;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public DriverDto editDriver(Long id, DriverDto updatedDriverDto) {
        Driver driverFromDB = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));

        driverMapper.updateDriverFromDriverDto(updatedDriverDto, driverFromDB);

        try {
            return driverMapper.driverToDriverDto(driverRepository.save(driverFromDB));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));

        driverRepository.delete(driver);
    }

    @Override
    public DriverDto getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));

        if (driver.getCar() != null) {
            Hibernate.initialize(driver.getCar());
        }

        return driverMapper.driverToDriverDto(driver);
    }

    @Override
    public List<DriverDto> getAllDrivers() {
        return driverRepository.findAll().stream().map(driverMapper::driverToDriverDto).toList();
    }
}
