package com.example.driver_service.service;

import com.example.driver_service.dto.CarDTO;

import java.util.List;

public interface CarService {

    CarDTO addCar(CarDTO carDTO);

    CarDTO editCar(CarDTO carDTO);

    void deleteCar(Long id);

    CarDTO getCarById(Long id);

    List<CarDTO> getAllCars();

}
