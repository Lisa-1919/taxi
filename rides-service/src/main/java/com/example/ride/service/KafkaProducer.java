package com.example.ride.service;

import com.example.ride.dto.UpdateStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.update-ride-status}")
    private String statusUpdateTopic;

    public void send(UpdateStatusMessage message) {
        kafkaTemplate.send(statusUpdateTopic, message);
        log.info("Sent update status message: {}", message);
    }
}
