package com.example.driver_service.service;

import com.example.driver_service.dto.PagedResponseDriverList;
import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import org.springframework.data.domain.Pageable;

public interface DriverService {

    ResponseDriver addDriver(RequestDriver requestDriver);

    ResponseDriver editDriver(Long id, RequestDriver requestDriver);

    void deleteDriver(Long id);

    ResponseDriver getDriverById(Long id);

    ResponseDriver getDriverByIdNonDeleted(Long id);

    PagedResponseDriverList getAllDrivers(Pageable pageable);

    PagedResponseDriverList getAllNonDeletedDrivers(Pageable pageable);

    boolean doesDriverExist(Long id);
}
