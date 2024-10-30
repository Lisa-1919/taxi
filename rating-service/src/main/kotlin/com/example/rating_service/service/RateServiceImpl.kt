package com.example.rating_service.service

import com.example.rating_service.dto.PagedResponseRateList
import com.example.rating_service.dto.RequestRate
import com.example.rating_service.dto.ResponseRate
import com.example.rating_service.entity.Rate
import com.example.rating_service.mapper.RateMapper
import com.example.rating_service.repo.RateRepository
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
open class RateServiceImpl(
    private val rateRepository: RateRepository,
    private val rateMapper: RateMapper
) : RateService {

    private val log = LoggerFactory.getLogger(RateServiceImpl::class.java)

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackRateResponse")
    override fun addRate(requestRate: RequestRate): ResponseRate {

        val rate = rateMapper.requestRateToRate(requestRate)
        val savedRate = rateRepository.save(rate)

        return rateMapper.rateToResponseRate(savedRate)
    }

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackRateResponse")
    override fun getRateById(id: Long): ResponseRate {
        val rate = rateRepository.getReferenceById(id)
        return rateMapper.rateToResponseRate(rate)
    }

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackPagedResponse")
    override fun getAllRates(pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.findAll(pageable)
        return createPagedResponse(ratePage)
    }

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackPagedResponse")
    override fun getAllRatesFromPassengers(pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesFromPassengers(pageable)
        return createPagedResponse(ratePage)
    }

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackPagedResponse")
    override fun getAllRatesFromDrivers(pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesFromDrivers(pageable)
        return createPagedResponse(ratePage)
    }

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackPagedResponse")
    override fun getAllRatesByPassengerId(passengerId: Long, pageable: Pageable): PagedResponseRateList {
        val ratePage = rateRepository.getAllRatesByPassengerId(passengerId, pageable)
        return createPagedResponse(ratePage)
    }

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackPagedResponse")
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

    fun fallbackRateResponse(id: Long, t: Throwable): ResponseRate {
        return ResponseRate(0L, 0L, null, 0L, 0.0, "Fallback rate");
    }

    fun fallbackPagedResponse(pageable: Pageable, t: Throwable): PagedResponseRateList {
        return PagedResponseRateList(emptyList<ResponseRate>(), pageable.pageNumber, pageable.pageSize, 0, 0, true)
    }

}