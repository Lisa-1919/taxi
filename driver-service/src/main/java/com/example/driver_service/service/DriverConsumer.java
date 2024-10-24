package com.example.driver_service.service;

import com.example.kafka.dto.CheckDriverRequest;
import com.example.kafka.dto.CheckDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class DriverConsumer {

    private final DriverService driverService;
    private final DriverProducer driverProducer;

    @KafkaListener(topics = "${kafka.topic.request-driver-check}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleDriverCheckRequest(CheckDriverRequest request) {
        log.info("Received driver check request for driverId: {}", request.driverId());

        boolean driverExists = driverService.doesDriverExist(request.driverId());

        CheckDriverResponse response = new CheckDriverResponse(request.rateId(), driverExists);

        driverProducer.sendDriverCheckResponse(response);
        log.info("Sent driver check response for rateId: {} with driverExists: {}", request.rateId(), driverExists);
    }

}
