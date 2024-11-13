package com.modsen.passenger.config;

import com.modsen.passenger.mapper.PassengerMapper;
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
