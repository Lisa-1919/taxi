package com.example.driver_service.config;

import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.mapper.DriverMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public DriverMapper driverMapper() {
        return Mappers.getMapper(DriverMapper.class);
    }

    @Bean
    public CarMapper carMapper() {
        return Mappers.getMapper(CarMapper.class);
    }

}
