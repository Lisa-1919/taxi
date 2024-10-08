package com.example.driver_service.service;

import com.example.driver_service.dto.DriverDto;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.DriverMapper;
import com.example.driver_service.repo.DriverRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public DriverDto addDriver(DriverDto driverDto) {
        try {
            Driver driver = driverMapper.driverDtoToDriver(driverDto);
            driverDto = driverMapper.driverToDriverDto(driverRepository.save(driver));
            return driverDto;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("A driver with that email or phone number already exists");
        }
    }

    @Override
    public DriverDto editDriver(Long id, DriverDto updatedDriverDto) {
        Driver driverFromDB = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        driverMapper.updateDriverFromDriverDto(updatedDriverDto, driverFromDB);

        try {
            return driverMapper.driverToDriverDto(driverRepository.save(driverFromDB));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("A driver with that email or phone number already exists");
        }
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        driverRepository.delete(driver);
    }

    @Override
    public DriverDto getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

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
