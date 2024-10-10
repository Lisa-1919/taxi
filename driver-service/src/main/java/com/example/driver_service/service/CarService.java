package com.example.driver_service.service;

import com.example.driver_service.dto.RequestCar;
import com.example.driver_service.dto.ResponseCar;
import com.example.driver_service.dto.ResponseCarList;

import java.util.List;

public interface CarService {

    ResponseCar addCar(RequestCar requestCar);

    ResponseCar editCar(Long id, RequestCar requestCar);

    void deleteCar(Long id);

    ResponseCar getCarById(Long id);

    ResponseCarList getAllCars();

}
