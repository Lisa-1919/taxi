package com.example.rides_service.service;

import com.example.kafka.dto.CheckRideResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.response-ride-check}")
    private String rideResponseTopic;

    public void sendPassengerCheckResponse(CheckRideResponse response) {
        kafkaTemplate.send(rideResponseTopic, response);
        log.info("Sent ride check response: {}", response);
    }
}
