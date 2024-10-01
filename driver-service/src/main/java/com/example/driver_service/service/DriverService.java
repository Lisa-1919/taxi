package com.example.driver_service.service;

import com.example.driver_service.dto.DriverDTO;

import java.util.List;

public interface DriverService {

    DriverDTO addDriver(DriverDTO driverDTO);

    DriverDTO editDriver(DriverDTO driverDTO);

    void deleteDriver(Long id);

    DriverDTO getById(Long id);

    List<DriverDTO> getAll();
}
