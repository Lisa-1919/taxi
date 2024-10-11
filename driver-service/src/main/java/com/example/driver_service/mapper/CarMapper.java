package com.example.driver_service.mapper;

import com.example.driver_service.dto.RequestCar;
import com.example.driver_service.dto.ResponseCar;
import com.example.driver_service.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface CarMapper {

    @Mapping(target = "driver.id", source = "driverId")
    Car requestCarToCar(RequestCar requestCar);

    @Mapping(target = "driverId", source = "driver.id")
    ResponseCar carToResponseCar(Car car);

    @Mapping(target = "driver.id", source = "driverId")
    void updateCarFromRequestCar(RequestCar requestCar, @MappingTarget Car carFromDB);
}
