package com.example.rides_service.service;

import com.example.kafka.dto.CheckRideRequest;
import com.example.kafka.dto.CheckRideResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class RideConsumer {

    private final RideService rideService;
    private final RideProducer rideProducer;

    @KafkaListener(topics = "${kafka.topic.request-ride-check}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleRideCheckRequest(CheckRideRequest request) {
        log.info("Received ride check request for passengerId: {}", request.rideId());
        boolean rideExists = rideService.doesRideExistForUser(request.rideId(), request.userId(), request.userType());

        CheckRideResponse response = new CheckRideResponse(request.rateId(), rideExists);

        rideProducer.sendPassengerCheckResponse(response);
        log.info("Sent ride check response for rateId: {} with rideExists: {}", request.rateId(), rideExists);
    }

}
