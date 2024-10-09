package com.example.driver_service.service;

import com.example.driver_service.dto.CarDto;
import com.example.driver_service.entity.Car;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.repo.CarRepository;
import com.example.driver_service.repo.DriverRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional
    public CarDto addCar(CarDto carDto) {

        Car car = carMapper.carDtoToCar(carDto);

        Driver driver = driverRepository.findById(carDto.driverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with ID: " + carDto.driverId()));

        car.setDriver(driver);
        try {
            car = carRepository.save(car);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("A car with that license plate already exists: " + carDto.licensePlate());
        }
        driver.setCar(car);
        driverRepository.save(driver);

        return carMapper.carToCarDto(car);
    }

    @Override
    @Transactional
    public CarDto editCar(Long id, CarDto updatedCarDto) {
        Car carFromDB = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        carMapper.updateCarFromCarDto(updatedCarDto, carFromDB);

        try {
            return carMapper.carToCarDto(carRepository.save(carFromDB));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("A car with that license plate already exists: " + updatedCarDto.licensePlate());
        }
    }

    @Override
    @Transactional
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        carRepository.delete(car);
    }

    @Override
    public CarDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        return carMapper.carToCarDto(car);
    }

    @Override
    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream().map(carMapper::carToCarDto).toList();
    }
}
