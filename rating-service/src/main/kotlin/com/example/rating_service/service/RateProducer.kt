package com.example.rating_service.service

import com.example.kafka.dto.CheckDriverRequest
import com.example.kafka.dto.CheckPassengerRequest
import com.example.kafka.dto.CheckRideRequest
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
open class RateProducer(
    @Value("\${kafka.topic.request-driver-check}")
    private val topicDriverRequest: String,
    @Value("\${kafka.topic.request-passenger-check}")
    private val topicPassengerRequest: String,
    @Value("\${kafka.topic.request-ride-check}")
    private val topicRideRequest: String,

    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(RateProducer::class.java)

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackCheckDriver")
    fun checkDriver(checkDriverRequest: CheckDriverRequest) {
        kafkaTemplate.send(topicDriverRequest, checkDriverRequest)
        log.info("Sent check driver request: {}", checkDriverRequest)
    }

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackCheckPassenger")
    fun checkPassenger(checkPassengerRequest: CheckPassengerRequest) {
        kafkaTemplate.send(topicPassengerRequest, checkPassengerRequest)
        log.info("Sent check passenger request: {}", checkPassengerRequest)
    }

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackCheckRide")
    fun checkRide(checkRideRequest: CheckRideRequest) {
        kafkaTemplate.send(topicRideRequest, checkRideRequest)
        log.info("Sent check ride request: {}", checkRideRequest)
    }

    fun fallbackCheckDriver(checkDriverRequest: CheckDriverRequest, throwable: Throwable) {
        log.error("Fallback for checkDriver due to: ${throwable.message}", throwable)
    }

    fun fallbackCheckPassenger(checkPassengerRequest: CheckPassengerRequest, throwable: Throwable) {
        log.error("Fallback for checkPassenger due to: ${throwable.message}", throwable)
    }

    fun fallbackCheckRide(checkRideRequest: CheckRideRequest, throwable: Throwable) {
        log.error("Fallback for checkRide due to: ${throwable.message}", throwable)
    }

}