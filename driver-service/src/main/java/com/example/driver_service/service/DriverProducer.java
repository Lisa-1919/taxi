package com.example.driver_service.service;

import com.example.kafka.dto.CheckDriverResponse;
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

    public void sendDriverCheckResponse(CheckDriverResponse response) {
        kafkaTemplate.send(driverResponseTopic, response);
        log.info("Sent driver check response: {}", response);
    }
}
