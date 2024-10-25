package com.example.rides_service.service;

import com.example.kafka.dto.CheckDriverResponse;
import com.example.kafka.dto.CheckRideResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    @CircuitBreaker(name="kafkaCircuitBreaker", fallbackMethod = "fallbackRideCheckResponse")
    public void sendRideCheckResponse(CheckRideResponse response) {
        kafkaTemplate.send(rideResponseTopic, response);
        log.info("Sent ride check response: {}", response);
    }

    private void fallbackRideCheckResponse(CheckDriverResponse response, Throwable t){
        log.error("Failed to send ride check response to Kafka. Reason: {}", t.getMessage());
    }
}
