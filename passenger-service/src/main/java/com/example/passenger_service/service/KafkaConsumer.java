package com.example.passenger_service.service;

import com.example.passenger_service.dto.UpdateStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {
    @KafkaListener(topics = "${kafka.topic.update-ride-status}", groupId = "${spring.kafka.consumer.group-id}")
    void listener(UpdateStatusMessage updateStatusMessage) {
        log.info(updateStatusMessage.message());
    }
}
