package com.example.driver_service.service;

import com.example.driver_service.dto.DriverDto;

import java.util.List;

public interface DriverService {

    DriverDto addDriver(DriverDto driverDto);

    DriverDto editDriver(Long id, DriverDto updatedDriverDto);

    void deleteDriver(Long id);

    DriverDto getDriverById(Long id);

    List<DriverDto> getAllDrivers();
}
