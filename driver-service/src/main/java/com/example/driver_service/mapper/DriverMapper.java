package com.example.driver_service.mapper;

import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = CarMapper.class)
public interface DriverMapper {

    Driver requestDriverToDriver(RequestDriver requestDriver);

    @Mapping(source = "car", target = "carDto")
    ResponseDriver driverToResponseDriver(Driver driver);

    void updateDriverFromRequestDriver(RequestDriver requestDriver, @MappingTarget Driver driverFromDB);
}