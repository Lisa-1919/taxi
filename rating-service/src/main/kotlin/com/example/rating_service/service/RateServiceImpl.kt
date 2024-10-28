package com.example.rating_service.service

import com.example.rating_service.client.DriverServiceClient
import com.example.rating_service.client.PassengerServiceClient
import com.example.rating_service.client.RideServiceClient
import com.example.rating_service.dto.PagedResponseRateList
import com.example.rating_service.dto.RequestRate
import com.example.rating_service.dto.ResponseRate
import com.example.rating_service.entity.Rate
import com.example.rating_service.mapper.RateMapper
import com.example.rating_service.repo.RateRepository
import com.example.rating_service.util.ExceptionMessages
import com.example.rating_service.util.UserType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RateServiceImpl(
    private val rateRepository: RateRepository,
    private val rateMapper: RateMapper,
    private val rideServiceClient: RideServiceClient,
    private val passengerServiceClient: PassengerServiceClient,
    private val driverServiceClient: DriverServiceClient
) : RateService {

    override fun addRate(requestRate: RequestRate): ResponseRate {
        isRideExists(requestRate.rideId, requestRate.userId, requestRate.userType)
        isUserExists(requestRate.userId, requestRate.userType)

        val rate = rateMapper.requestRateToRate(requestRate)
        val savedRate = rateRepository.save(rate)

        return rateMapper.rateToResponseRate(savedRate)
    }

    override fun getRateById(id: Long): ResponseRate {
        val rate = rateRepository.getReferenceById(id)
        return rateMapper.rateToResponseRate(rate)
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

    private fun isRideExists(rideId: Long, userId: Long, userType: UserType): Boolean? =
        when(userType) {
            UserType.DRIVER -> rideServiceClient.doesRideExistForDriver(rideId, userId).body
            UserType.PASSENGER -> rideServiceClient.doesRideExistForPassenger(rideId, userId).body
        }

    private fun isUserExists(userId: Long, userType: UserType): Boolean? =
        when (userType) {
            UserType.DRIVER -> driverServiceClient.doesDriverExist(userId).body
            UserType.PASSENGER -> passengerServiceClient.doesPassengerExist(userId).body
        }

    private fun createPagedResponse(ratePage: Page<Rate>): PagedResponseRateList {
        val rates = ratePage.map(rateMapper::rateToResponseRate).toList()
        return PagedResponseRateList(
            list = rates,
            pageNumber = ratePage.number,
            pageSize = ratePage.size,
            totalElements = ratePage.totalElements,
            totalPages = ratePage.totalPages,
            last = ratePage.isLast
        )
    }

}