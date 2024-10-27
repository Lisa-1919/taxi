package com.example.driver_service.service;

import com.example.kafka.dto.CheckDriverResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.response-driver-check}")
    private String driverResponseTopic;

    @CircuitBreaker(name = "kafkaCircuitBreaker", fallbackMethod = "fallbackDriverCheckResponse")
    public void sendDriverCheckResponse(CheckDriverResponse response) {
        kafkaTemplate.send(driverResponseTopic, response);
        log.info("Sent driver check response: {}", response);
    }

    private void fallbackDriverCheckResponse(CheckDriverResponse response, Throwable t){
        log.error("Failed to send driver check response to Kafka. Reason: {}", t.getMessage());
    }
}
