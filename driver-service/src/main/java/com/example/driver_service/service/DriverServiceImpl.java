package com.example.driver_service.service;

import com.example.driver_service.dto.CarDto;
import com.example.driver_service.dto.DriverDto;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.mapper.DriverMapper;
import com.example.driver_service.repo.DriverRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarMapper carMapper;

    @Override
    public DriverDto addDriver(DriverDto driverDto) {
        Driver driver = driverMapper.driverDtoToDriver(driverDto);

        try {
            driver = driverRepository.save(driver);
        } catch (DataIntegrityViolationException ex){
            throw new IllegalArgumentException("A driver with that email or phoneNumber already exists");
        }

        return driverMapper.driverToDriverDto(driver);
    }

    @Override
    public DriverDto editDriver(DriverDto driverDto) {
        Driver driverFromDB = driverRepository.findById(driverDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        driverMapper.updateDriverFromDriverDto(driverDto, driverFromDB);

        CarDto carDTO = carMapper.carToCarDto(driverFromDB.getCar());
        try {
            DriverDto updatedDriverDto = driverMapper.driverToDriverDto(driverRepository.save(driverFromDB));

            updatedDriverDto.setCarDto(carDTO);

            return updatedDriverDto;
        } catch (DataIntegrityViolationException ex){
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

        DriverDto driverDto = driverMapper.driverToDriverDto(driver);

        CarDto carDto = carMapper.carToCarDto(driver.getCar());
        driverDto.setCarDto(carDto);

        return driverDto;
    }

    @Override
    public List<DriverDto> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();

        return drivers.stream()
                .map(driver -> {

                    if (driver.getCar() != null) {
                        Hibernate.initialize(driver.getCar());
                    }

                    DriverDto driverDto = driverMapper.driverToDriverDto(driver);

                    if (driver.getCar() != null) {
                        CarDto carDto = carMapper.carToCarDto(driver.getCar());
                        driverDto.setCarDto(carDto);
                    }

                    return driverDto;
                })
                .collect(Collectors.toList());
    }
}
