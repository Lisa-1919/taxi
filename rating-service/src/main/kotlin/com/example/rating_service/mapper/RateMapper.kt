package com.example.rating_service.mapper

import com.example.rating_service.dto.RequestRate
import com.example.rating_service.dto.ResponseRate
import com.example.rating_service.entity.Rate
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface RateMapper {

    fun rateToResponseRate(rate: Rate): ResponseRate
    fun requestRateToRate(requestRate: RequestRate): Rate
}