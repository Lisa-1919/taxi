package com.modsen.passenger.service;

import com.modsen.passenger.dto.UpdateStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {
    @KafkaListener(topics = "${kafka.topic.update-ride-status}", groupId = "${spring.kafka.consumer.group-id}")
    public void listener(UpdateStatusMessage updateStatusMessage) {
        log.info(updateStatusMessage.message());
    }
}
