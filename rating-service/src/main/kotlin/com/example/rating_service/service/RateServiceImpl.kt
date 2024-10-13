package com.example.rating_service.service

import com.example.rating_service.dto.RequestRate
import com.example.rating_service.dto.ResponseRate
import com.example.rating_service.dto.ResponseRateList
import com.example.rating_service.entity.Rate
import com.example.rating_service.repo.RateRepository
import org.springframework.stereotype.Service

@Service
class RateServiceImpl(
    private val rateRepository: RateRepository
) : RateService {

    override fun addRate(requestRate: RequestRate): ResponseRate {
        isRideExists(requestRate.rideId)
        isDriverExists(requestRate.driverId)
        isPassengerExists(requestRate.passengerId)

        val rate = getRate(requestRate)
        val savedRate = rateRepository.save(rate)

        return getResponseRate(savedRate)
    }

    override fun getRateById(id: Long): ResponseRate {
        val rate = rateRepository.getReferenceById(id)
        return getResponseRate(rate)
    }

    override fun getAllRates(): ResponseRateList {
        return ResponseRateList(rateRepository.findAll().stream().map(::getResponseRate).toList())
    }

    private fun isRideExists(rideId: Long): Boolean {
        return true;
    }

    private fun isDriverExists(driverId: Long): Boolean {
        return true;
    }

    private fun isPassengerExists(passengerId: Long): Boolean {
        return true;
    }


    private fun getRate(requestRate: RequestRate): Rate {
        return Rate(
            id = 0L,
            driverId = requestRate.driverId,
            passengerId = requestRate.passengerId,
            rideId = requestRate.rideId,
            rate = requestRate.rate,
            rideCommentary = requestRate.rideCommentary
        )
    }

    private fun getResponseRate(rate: Rate): ResponseRate {
        return ResponseRate(
            id = rate.id,
            driverId = rate.driverId,
            passengerId = rate.passengerId,
            rideId = rate.rideId,
            rate = rate.rate,
            rideCommentary = rate.rideCommentary
        )
    }

}