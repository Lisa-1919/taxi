package com.example.passenger_service.config;

import com.example.passenger_service.mapper.PassengerMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public PassengerMapper passengerMapper() {
        return Mappers.getMapper(PassengerMapper.class);
    }

}
