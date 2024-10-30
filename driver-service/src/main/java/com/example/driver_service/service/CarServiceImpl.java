package com.example.driver_service.service;

import com.example.driver_service.dto.*;
import com.example.driver_service.entity.Car;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.repo.CarRepository;
import com.example.driver_service.repo.DriverRepository;
import com.example.driver_service.util.ExceptionMessages;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {

    private static final String LICENSE_PLATE = "licensePlate";

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional
    @CircuitBreaker(name = "carService", fallbackMethod = "fallbackCarResponse")
    public ResponseCar addCar(RequestCar requestCar) {

        checkUniqueField(LICENSE_PLATE, requestCar.licensePlate(), carRepository::existsByLicensePlate);

        Car car = carMapper.requestCarToCar(requestCar);

        Driver driver = driverRepository.findDriverByIdNonDeleted(requestCar.driverId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(requestCar.driverId())));

        car.setDriver(driver);
        car = carRepository.save(car);
        driver.setCar(car);
        driverRepository.save(driver);

        return carMapper.carToResponseCar(car);
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "carService", fallbackMethod = "fallbackCarResponse")
    public ResponseCar editCar(Long id, RequestCar requestCar) {
        Car carFromDB = getOrThrow(id);

        if (!carFromDB.getLicensePlate().equals(requestCar.licensePlate())) {
            checkUniqueField(LICENSE_PLATE, requestCar.licensePlate(), carRepository::existsByLicensePlate);
        }

        carMapper.updateCarFromRequestCar(requestCar, carFromDB);

        return carMapper.carToResponseCar(carRepository.save(carFromDB));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "carService", fallbackMethod = "fallbackVoidResponse")
    public void deleteCar(Long id) {
        Car car = getOrThrow(id);
        carRepository.delete(car);
    }

    @Override
    @CircuitBreaker(name = "carService", fallbackMethod = "fallbackCarResponse")
    public ResponseCar getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));

        return carMapper.carToResponseCar(car);
    }

    @Override
    @CircuitBreaker(name = "carService", fallbackMethod = "fallbackCarResponse")
    public ResponseCar getCarByIdNonDeleted(Long id) {
        Car car = getOrThrow(id);
        return carMapper.carToResponseCar(car);
    }

    @Override
    @CircuitBreaker(name = "carService", fallbackMethod = "fallbackPagedResponse")
    public PagedResponseCarList getAllCars(Pageable pageable) {
        Page<Car> carsPage = carRepository.findAll(pageable);
        return getPagedResponseCarListFromPage(carsPage);
    }

    @Override
    @CircuitBreaker(name = "carService", fallbackMethod = "fallbackPagedResponse")
    public PagedResponseCarList getAllNonDeletedCars(Pageable pageable) {
        Page<Car> carsPage = carRepository.findAllNonDeleted(pageable);
        return getPagedResponseCarListFromPage(carsPage);
    }

    private Car getOrThrow(Long id) {
        return carRepository.findCarByIdNonDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));
    }

    private <T> void checkUniqueField(String fieldName, T fieldValue, Predicate<T> existsFunction) {
        if (existsFunction.test(fieldValue)) {
            throw new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_CAR_ERROR.format(fieldName, fieldValue));
        }
    }

    private PagedResponseCarList getPagedResponseCarListFromPage(Page<Car> carPage) {
        List<ResponseCar> responseCarList = carPage
                .map(carMapper::carToResponseCar)
                .toList();

        return new PagedResponseCarList(
                responseCarList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast()
        );
    }

    public ResponseCar fallbackCarResponse(Long id, Throwable t) {
        return new ResponseCar(0L, null, null, null, 0L, null);
    }

    public PagedResponseCarList fallbackPagedResponse(Pageable pageable, Throwable t) {
        return new PagedResponseCarList(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0, true);
    }

    public void fallbackVoidResponse(Long id, Throwable t) {
        log.error("Failed to process request for car id: {}. Error: {}", id, t.getMessage(), t);
    }

}
