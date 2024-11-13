package com.modsen.driver.service;

import com.modsen.driver.dto.RequestCar;
import com.modsen.driver.dto.ResponseCar;
import com.modsen.driver.dto.PagedResponseCarList;
import org.springframework.data.domain.Pageable;

public interface CarService {

    ResponseCar addCar(RequestCar requestCar);

    ResponseCar editCar(Long id, RequestCar requestCar);

    void deleteCar(Long id);

    ResponseCar getCarById(Long id);

    ResponseCar getCarByIdNonDeleted(Long id);

    PagedResponseCarList getAllCars(Pageable pageable);

    PagedResponseCarList getAllNonDeletedCars(Pageable pageable);

}
