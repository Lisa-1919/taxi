package com.modsen.rating.service

import com.modsen.rating.dto.RequestRate
import com.modsen.rating.dto.ResponseRate
import com.modsen.rating.dto.PagedResponseRateList
import org.springframework.data.domain.Pageable

interface RateService {

    fun addRate(requestRate: RequestRate): ResponseRate
    fun getRateById(id: Long): ResponseRate
    fun getAllRates(pageable: Pageable): PagedResponseRateList
    fun getAllRatesFromPassengers(pageable: Pageable): PagedResponseRateList
    fun getAllRatesFromDrivers(pageable: Pageable): PagedResponseRateList
    fun getAllRatesByPassengerId(passengerId: Long, pageable: Pageable): PagedResponseRateList
    fun getAllRatesByDriverId(driverId: Long, pageable: Pageable): PagedResponseRateList
}