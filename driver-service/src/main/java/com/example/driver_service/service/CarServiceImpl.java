package com.example.driver_service.service;

import com.example.driver_service.dto.*;
import com.example.driver_service.entity.Car;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.repo.CarRepository;
import com.example.driver_service.repo.DriverRepository;
import com.example.driver_service.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseCar addCar(RequestCar requestCar) {

        Car car = carMapper.requestCarToCar(requestCar);

        Driver driver = driverRepository.getDriverByIdNonDeleted(requestCar.driverId())
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
            throw new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_CAR_ERROR.format(requestCar.licensePlate()));
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
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));

        return carMapper.carToResponseCar(car);
    }

    @Override
    public ResponseCar getCarByIdNonDeleted(Long id) {
        Car car = getOrThrow(id);
        return carMapper.carToResponseCar(car);
    }

    @Override
    public PagedResponseCarList getAllCars(Pageable pageable) {
        Page<Car> carsPage = carRepository.findAll(pageable);
        return getPagedResponseCarListFromPage(carsPage);
    }

    @Override
    public PagedResponseCarList getAllNonDeletedCars(Pageable pageable) {
        Page<Car> carsPage = carRepository.getAllNonDeleted(pageable);
        return getPagedResponseCarListFromPage(carsPage);
    }

    private Car getOrThrow(Long id) {
        return carRepository.getCarByIdNonDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));
    }

    private PagedResponseCarList getPagedResponseCarListFromPage(Page<Car> carPage){
        List<ResponseCar> responseCarList = carPage.stream()
                .map(carMapper::carToResponseCar).toList();

        return new PagedResponseCarList(
                responseCarList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast()
        );
    }

}
