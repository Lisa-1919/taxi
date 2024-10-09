package com.example.driver_service.service;

import com.example.driver_service.dto.CarDto;
import com.example.driver_service.entity.Car;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.repo.CarRepository;
import com.example.driver_service.repo.DriverRepository;
import com.example.driver_service.util.ExceptionMessages;
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
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(carDto.driverId())));

        car.setDriver(driver);
        try {
            car = carRepository.save(car);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_CAR_ERROR.format(carDto.licensePlate()));
        }
        driver.setCar(car);
        driverRepository.save(driver);

        return carMapper.carToCarDto(car);
    }

    @Override
    @Transactional
    public CarDto editCar(Long id, CarDto updatedCarDto) {
        Car carFromDB = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));

        carMapper.updateCarFromCarDto(updatedCarDto, carFromDB);

        try {
            return carMapper.carToCarDto(carRepository.save(carFromDB));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_CAR_ERROR.format(updatedCarDto.licensePlate()));
        }
    }

    @Override
    @Transactional
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));

        carRepository.delete(car);
    }

    @Override
    public CarDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));

        return carMapper.carToCarDto(car);
    }

    @Override
    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream().map(carMapper::carToCarDto).toList();
    }
}
