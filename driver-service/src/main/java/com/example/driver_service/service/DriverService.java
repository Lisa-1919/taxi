package com.example.driver_service.service;

import com.example.driver_service.dto.RequestCar;
import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.dto.ResponseDriverList;

import java.util.List;

public interface DriverService {

    ResponseDriver addDriver(RequestDriver requestDriver);

    ResponseDriver editDriver(Long id, RequestDriver requestDriver);

    void deleteDriver(Long id);

    ResponseDriver getDriverById(Long id);

    ResponseDriverList getAllDrivers();
}
