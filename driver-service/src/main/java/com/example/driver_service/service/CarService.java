package com.example.driver_service.service;

import com.example.driver_service.dto.CarDto;

import java.util.List;

public interface CarService {

    CarDto addCar(CarDto carDto);

    CarDto editCar(Long id, CarDto updatedCarDto);

    void deleteCar(Long id);

    CarDto getCarById(Long id);

    List<CarDto> getAllCars();

}
