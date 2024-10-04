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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final DriverRepository driverRepository;


    @Override
    public CarDto addCar(CarDto carDto) {
        Car car = carMapper.carDtoToCar(carDto);

        Driver driver = driverRepository.findById(carDto.getDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with ID: " + carDto.getDriverId()));

        car.setDriver(driver);
        try {
            car = carRepository.save(car);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("A car with that license plate already exists: " + carDto.getLicensePlate());
        }
        driver.setCar(car);
        driverRepository.save(driver);

        return carMapper.carToCarDto(car);
    }

    @Override
    public CarDto editCar(CarDto carDto) {
        Car carFromDB = carRepository.findById(carDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        carMapper.updateCarFromCarDto(carDto, carFromDB);

        try {
            return carMapper.carToCarDto(carRepository.save(carFromDB));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("A car with that license plate already exists: " + carDto.getLicensePlate());
        }
    }

    @Override
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
        List<Car> cars = carRepository.findAll();

        return cars.stream()
                .map(carMapper::carToCarDto)
                .collect(Collectors.toList());
    }
}
