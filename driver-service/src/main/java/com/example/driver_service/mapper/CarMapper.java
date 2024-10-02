package com.example.driver_service.mapper;

import com.example.driver_service.dto.CarDTO;
import com.example.driver_service.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface CarMapper {

    Car carDTOToCar(CarDTO carDTO);
    CarDTO carToCarDTO(Car car);
    void updateCarFromCarDTO(CarDTO carDTO, @MappingTarget Car car);
}
