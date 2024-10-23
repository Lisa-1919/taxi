package com.example.passenger_service.service;

import com.example.kafka.dto.CheckPassengerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.response-passenger-check}")
    private String passengerResponseTopic;

    public void sendPassengerCheckResponse(CheckPassengerResponse response) {
        kafkaTemplate.send(passengerResponseTopic, response);
        log.info("Sent passenger check response: {}", response);
    }
}
