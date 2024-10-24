package com.example.rating_service.service

import com.example.kafka.dto.CheckDriverResponse
import com.example.kafka.dto.CheckPassengerResponse
import com.example.kafka.dto.CheckRideResponse
import com.example.kafka.util.CheckResponseType
import com.example.rating_service.repo.RateRepository
import com.example.rating_service.util.ExceptionMessages
import com.example.rating_service.util.RateStatus
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class RateConsumer(
    @Value("\${kafka.topic.response-driver-check}")
    private val topicDriverResponse: String,
    @Value("\${kafka.topic.response-passenger-check}")
    private val topicPassengerResponse: String,
    @Value("\${kafka.topic.response-ride-check}")
    private val topicRideResponse: String,
    private val rateRepository: RateRepository
) {

    private val log = LoggerFactory.getLogger(RateConsumer::class.java)

    @KafkaListener(topics = ["\${kafka.topic.response-driver-check}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun listenDriverResponse(driverResponse: CheckDriverResponse) {
        log.info("Received driver response for rateId: ${driverResponse.rateId}, exists: ${driverResponse.isExist}")
        processResponse(driverResponse.rateId, driverResponse.isExist, CheckResponseType.DRIVER)
    }

    @KafkaListener(topics = ["\${kafka.topic.response-passenger-check}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun listenPassengerResponse(passengerResponse: CheckPassengerResponse) {
        log.info("Received passenger response for rateId: ${passengerResponse.rateId}, exists: ${passengerResponse.isExist}")
        processResponse(passengerResponse.rateId, passengerResponse.isExist, CheckResponseType.PASSENGER)
    }

    @KafkaListener(topics = ["\${kafka.topic.response-ride-check}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun listenRideResponse(rideResponse: CheckRideResponse) {
        log.info("Received ride response for rateId: ${rideResponse.rateId}, exists: ${rideResponse.isExist}")
        processResponse(rideResponse.rateId, rideResponse.isExist, CheckResponseType.RIDE)
    }

    private fun processResponse(rateId: Long, exists: Boolean, entityType: CheckResponseType) {
        val rate = rateRepository.findById(rateId).orElseThrow {
            EntityNotFoundException(ExceptionMessages.RATE_NOT_FOUND.format(rateId))
        }

        rate.status = if (exists) {
            log.info("Entity $entityType exists for rateId: $rateId, setting status to VALID")
            RateStatus.VALID
        } else {
            log.warn("Entity $entityType does not exist for rateId: $rateId, setting status to INVALID")
            RateStatus.INVALID
        }

        rateRepository.save(rate)
        log.info("Rate status updated for rateId: $rateId to ${rate.status}")
    }
}