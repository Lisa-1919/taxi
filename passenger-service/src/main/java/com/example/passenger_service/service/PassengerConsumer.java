package com.example.passenger_service.service;

import com.example.kafka.dto.CheckPassengerRequest;
import com.example.kafka.dto.CheckPassengerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerConsumer {

    private final PassengerService passengerService;
    private final PassengerProducer passengerProducer;

    @KafkaListener(topics = "${kafka.topic.request-passenger-check}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePassengerCheckRequest(CheckPassengerRequest request) {
        log.info("Received passenger check request for passengerId: {}", request.passengerId());
        boolean passengerExists = passengerService.doesPassengerExist(request.passengerId());

        CheckPassengerResponse response = new CheckPassengerResponse(request.rateId(), passengerExists);

        passengerProducer.sendPassengerCheckResponse(response);
        log.info("Sent passenger check response for rateId: {} with passengerExists: {}", request.rateId(), passengerExists);
    }

}
