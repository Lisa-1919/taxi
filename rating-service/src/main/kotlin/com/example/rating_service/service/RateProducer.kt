package com.example.rating_service.service

import com.example.kafka.dto.CheckDriverRequest
import com.example.kafka.dto.CheckPassengerRequest
import com.example.kafka.dto.CheckRideRequest
import com.example.rating_service.exception.GlobalExceptionHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class RateProducer(
    @Value("\${kafka.topic.request-driver-check}")
    private val topicDriverRequest: String,
    @Value("\${kafka.topic.request-passenger-check}")
    private val topicPassengerRequest: String,
    @Value("\${kafka.topic.request-ride-check}")
    private val topicRideRequest: String,

    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    fun checkDriver(checkDriverRequest: CheckDriverRequest) {
        kafkaTemplate.send(topicDriverRequest, checkDriverRequest)
        log.info("Sent check driver request: {}", checkDriverRequest)
    }

    fun checkPassenger(checkPassengerRequest: CheckPassengerRequest) {
        kafkaTemplate.send(topicPassengerRequest, checkPassengerRequest)
        log.info("Sent check passenger request: {}", checkPassengerRequest)
    }

    fun checkRide(checkRideRequest: CheckRideRequest) {
        kafkaTemplate.send(topicRideRequest, checkRideRequest)
        log.info("Sent check ride request: {}", checkRideRequest)
    }

}