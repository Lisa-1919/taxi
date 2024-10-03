package com.example.driver_service.service;

import com.example.driver_service.dto.CarDTO;
import com.example.driver_service.dto.DriverDTO;
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
    public DriverDTO addDriver(DriverDTO driverDTO) {
        Driver driver = driverMapper.driverDTOToDriver(driverDTO);

        try {
            driver = driverRepository.save(driver);
        } catch (DataIntegrityViolationException ex){
            throw new IllegalArgumentException("A driver with that email or phoneNumber already exists");
        }

        return driverMapper.driverToDriverDTO(driver);
    }

    @Override
    public DriverDTO editDriver(DriverDTO driverDTO) {
        Driver driverFromDB = driverRepository.findById(driverDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        driverMapper.updateDriverFromDriverDTO(driverDTO, driverFromDB);

        CarDTO carDTO = carMapper.carToCarDTO(driverFromDB.getCar());
        try {
            DriverDTO updatedDriverDTO = driverMapper.driverToDriverDTO(driverRepository.save(driverFromDB));

            updatedDriverDTO.setCarDTO(carDTO);

            return updatedDriverDTO;
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
    public DriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        if (driver.getCar() != null) {
            Hibernate.initialize(driver.getCar());
        }

        DriverDTO driverDTO = driverMapper.driverToDriverDTO(driver);

        CarDTO carDTO = carMapper.carToCarDTO(driver.getCar());
        driverDTO.setCarDTO(carDTO);

        return driverDTO;
    }

    @Override
    public List<DriverDTO> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();

        return drivers.stream()
                .map(driver -> {

                    if (driver.getCar() != null) {
                        Hibernate.initialize(driver.getCar());
                    }

                    DriverDTO driverDTO = driverMapper.driverToDriverDTO(driver);

                    if (driver.getCar() != null) {
                        CarDTO carDTO = carMapper.carToCarDTO(driver.getCar());
                        driverDTO.setCarDTO(carDTO);
                    }

                    return driverDTO;
                })
                .collect(Collectors.toList());
    }
}
