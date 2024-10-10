package com.example.driver_service.service;

import com.example.driver_service.dto.RequestCar;
import com.example.driver_service.dto.ResponseCar;
import com.example.driver_service.dto.ResponseCarList;
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

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional
    public ResponseCar addCar(RequestCar requestCar) {

        Car car = carMapper.requestCarToCar(requestCar);

        Driver driver = driverRepository.findById(requestCar.driverId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(requestCar.driverId())));

        car.setDriver(driver);
        try {
            car = carRepository.save(car);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_CAR_ERROR.format(requestCar.licensePlate()));
        }
        driver.setCar(car);
        driverRepository.save(driver);

        return carMapper.carToResponseCar(car);
    }

    @Override
    @Transactional
    public ResponseCar editCar(Long id, RequestCar requestCar) {
        Car carFromDB = getOrThrow(id);
        carMapper.updateCarFromRequestCar(requestCar, carFromDB);

        try {
            return carMapper.carToResponseCar(carRepository.save(carFromDB));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ExceptionMessages.DUPLICATE_CAR_ERROR.format(requestCar.licensePlate()));
        }
    }

    @Override
    @Transactional
    public void deleteCar(Long id) {
        Car car = getOrThrow(id);
        carRepository.delete(car);
    }

    @Override
    public ResponseCar getCarById(Long id) {
        Car car = getOrThrow(id);
        return carMapper.carToResponseCar(car);
    }

    @Override
    public ResponseCarList getAllCars() {
        return new ResponseCarList(carRepository.findAll().stream().map(carMapper::carToResponseCar).toList());
    }

    private Car getOrThrow(Long id){
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));
    }

}
