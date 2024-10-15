package com.example.rating_service.service

import com.example.rating_service.dto.RequestRate
import com.example.rating_service.dto.ResponseRate
import com.example.rating_service.dto.PagedResponseRateList
import com.example.rating_service.entity.Rate
import com.example.rating_service.repo.RateRepository
import com.example.rating_service.util.UserType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RateServiceImpl(
    private val rateRepository: RateRepository
) : RateService {

    override fun addRate(requestRate: RequestRate): ResponseRate {
        isRideExists(requestRate.rideId)
        isUserExists(requestRate.userId, requestRate.userType)

        val rate = getRate(requestRate)
        val savedRate = rateRepository.save(rate)

        return getResponseRate(savedRate)
    }

    override fun getRateById(id: Long): ResponseRate {
        val rate = rateRepository.getReferenceById(id)
        return getResponseRate(rate)
    }

    override fun getAllRates(pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.findAll(pageable)
        return createPagedResponse(ratePage)
    }

    override fun getAllRatesFromPassengers(pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesFromPassengers(pageable)
        return createPagedResponse(ratePage)
    }

    override fun getAllRatesFromDrivers(pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesFromDrivers(pageable)
        return createPagedResponse(ratePage)
    }

    override fun getAllRatesByPassengerId(passengerId: Long, pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesByPassengerId(passengerId, pageable)
        return createPagedResponse(ratePage)
    }

    override fun getAllRatesByDriverId(driverId: Long, pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesByDriverId(driverId, pageable)
        return createPagedResponse(ratePage)
    }

    //TODO: request on ride-service
    private fun isRideExists(rideId: Long): Boolean {
        return true;
    }

    private fun isUserExists(userId: Long, userType: UserType): Boolean {
        return when (userType) {
            UserType.DRIVER -> isDriverExists(userId)
            UserType.PASSENGER -> isPassengerExists(userId)
        }
    }

    //TODO: request on driver-service
    private fun isDriverExists(driverId: Long): Boolean {
        return true;
    }

    //TODO: request on passenger-service
    private fun isPassengerExists(passengerId: Long): Boolean {
        return true;
    }


    private fun getRate(requestRate: RequestRate): Rate {
        return Rate(
            id = 0L,
            userId = requestRate.userId,
            userType = requestRate.userType,
            rideId = requestRate.rideId,
            rate = requestRate.rate,
            rideCommentary = requestRate.rideCommentary
        )
    }

    private fun getResponseRate(rate: Rate): ResponseRate {
        return ResponseRate(
            id = rate.id,
            userId = rate.userId,
            userType = rate.userType,
            rideId = rate.rideId,
            rate = rate.rate,
            rideCommentary = rate.rideCommentary
        )
    }

    private fun createPagedResponse(ratePage: Page<Rate>): PagedResponseRateList {
        val rates = ratePage.map(::getResponseRate).toList()
        return PagedResponseRateList(
            rates = rates,
            pageNumber = ratePage.number,
            pageSize = ratePage.size,
            totalElements = ratePage.totalElements,
            totalPages = ratePage.totalPages,
            last = ratePage.isLast
        )
    }

}