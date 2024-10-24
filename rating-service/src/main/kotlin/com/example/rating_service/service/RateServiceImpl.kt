package com.example.rating_service.service

import com.example.kafka.dto.CheckDriverRequest
import com.example.kafka.dto.CheckPassengerRequest
import com.example.kafka.dto.CheckRideRequest
import com.example.rating_service.dto.PagedResponseRateList
import com.example.rating_service.dto.RequestRate
import com.example.rating_service.dto.ResponseRate
import com.example.rating_service.entity.Rate
import com.example.rating_service.mapper.RateMapper
import com.example.rating_service.repo.RateRepository
import com.example.kafka.util.UserType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RateServiceImpl(
    private val rateRepository: RateRepository,
    private val rateMapper: RateMapper,
    private val rateProducer: RateProducer
) : RateService {

    override fun addRate(requestRate: RequestRate): ResponseRate {

        val  rate = rateMapper.requestRateToRate(requestRate)
        val savedRate = rateRepository.save(rate)

        rateProducer.checkRide(CheckRideRequest(savedRate.id, requestRate.rideId, requestRate.userId, requestRate.userType))
        when(requestRate.userType){
            UserType.PASSENGER-> rateProducer.checkPassenger(CheckPassengerRequest(savedRate.id, requestRate.userId))
            UserType.DRIVER -> rateProducer.checkDriver(CheckDriverRequest(savedRate.id, requestRate.userId))
        }

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