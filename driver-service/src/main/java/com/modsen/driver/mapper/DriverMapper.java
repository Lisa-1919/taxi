package com.modsen.driver.mapper;

import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.dto.ResponseDriver;
import com.modsen.driver.entity.Driver;
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