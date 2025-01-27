package com.modsen.rating.service

import com.modsen.rating.client.DriverServiceClient
import com.modsen.rating.client.PassengerServiceClient
import com.modsen.rating.client.RideServiceClient
import com.modsen.rating.dto.PagedResponseRateList
import com.modsen.rating.dto.RequestRate
import com.modsen.rating.dto.ResponseRate
import com.modsen.rating.entity.Rate
import com.modsen.rating.mapper.RateMapper
import com.modsen.rating.repo.RateRepository
import com.modsen.rating.util.ExceptionMessages
import com.modsen.rating.util.UserType
import jakarta.persistence.EntityNotFoundException
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
open class RateServiceImpl(
    private val rateRepository: RateRepository,
    private val rateMapper: RateMapper,
    private val rideServiceClient: RideServiceClient,
    private val passengerServiceClient: com.modsen.rating.client.PassengerServiceClient,
    private val driverServiceClient: com.modsen.rating.client.DriverServiceClient
) : RateService {

    private val log = LoggerFactory.getLogger(RateServiceImpl::class.java)

    override fun addRate(requestRate: RequestRate): ResponseRate {
        isRideExists(requestRate.rideId, requestRate.userId, requestRate.userType)
        isUserExists(requestRate.userId, requestRate.userType)

        val rate = rateMapper.requestRateToRate(requestRate)
        val savedRate = rateRepository.save(rate)

        return rateMapper.rateToResponseRate(savedRate)
    }

    override fun getRateById(id: Long): ResponseRate {
        val rate = rateRepository.findById(id)
            .orElseThrow{ EntityNotFoundException(ExceptionMessages.rateNotFound(id)) }
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

    override fun getAllRatesByPassengerId(passengerId: UUID, pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesByPassengerId(passengerId, pageable)
        return createPagedResponse(ratePage)
    }

    override fun getAllRatesByDriverId(driverId: UUID, pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesByDriverId(driverId, pageable)
        return createPagedResponse(ratePage)
    }

    private fun isRideExists(rideId: Long, userId: UUID, userType: UserType): Boolean? =
        when(userType) {
            UserType.DRIVER -> rideServiceClient.doesRideExistForDriver(rideId, userId).body
            UserType.PASSENGER -> rideServiceClient.doesRideExistForPassenger(rideId, userId).body
        }

    private fun isUserExists(userId: UUID, userType: UserType): Boolean? =
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