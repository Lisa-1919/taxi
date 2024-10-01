package com.example.driver_service.service;

import com.example.driver_service.dto.DriverDTO;
import com.example.driver_service.entity.Car;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.mapper.DriverMapper;
import com.example.driver_service.repo.CarRepository;
import com.example.driver_service.repo.DriverRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public DriverDTO addDriver(DriverDTO driverDTO) {
        Driver driver = driverMapper.driverDTOToDriver(driverDTO);

        if (driverDTO.getCarDTO() != null) {
            Car car = carMapper.carDTOToCar(driverDTO.getCarDTO());
            driver.setCar(car);
        }

        driver = driverRepository.save(driver);
        return driverMapper.driverToDriverDTO(driver);
    }

    @Override
    public DriverDTO editDriver(DriverDTO driverDTO) {
        Driver driverFromDB = driverRepository.findById(driverDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        if (driverDTO.getCarDTO() != null && driverDTO.getCarDTO().getId() != null) {
            Car car = carRepository.findById(driverDTO.getCarDTO().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Car not found"));
            driverFromDB.setCar(car);
        }

        driverMapper.updateDriverFromDriverDTO(driverDTO, driverFromDB);

        return driverMapper.driverToDriverDTO(driverRepository.save(driverFromDB));
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

        Hibernate.initialize(driver.getCar());

        return driverMapper.driverToDriverDTO(driver);
    }

    @Override
    public List<DriverDTO> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return drivers.stream()
                .map(driverMapper::driverToDriverDTO)
                .collect(Collectors.toList());
    }
}
