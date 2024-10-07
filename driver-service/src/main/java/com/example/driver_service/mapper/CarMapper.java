package com.example.driver_service.mapper;

import com.example.driver_service.dto.CarDto;
import com.example.driver_service.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface CarMapper {

    @Mapping(target = "driver.id", source = "driverId")
    Car carDtoToCar(CarDto carDto);

    @Mapping(target = "driverId", source = "driver.id")
    CarDto carToCarDto(Car car);

    @Mapping(target = "driver.id", source = "driverId")
    void updateCarFromCarDto(CarDto updatedCarDto, @MappingTarget Car carFromDB);
}
