package com.example.rating_service.service

import com.example.rating_service.dto.RequestRate
import com.example.rating_service.dto.ResponseRate
import com.example.rating_service.dto.ResponseRateList

interface RateService {

    fun addRate(requestRate: RequestRate): ResponseRate
    fun getRateById(id: Long): ResponseRate
    fun getAllRates(): ResponseRateList
}