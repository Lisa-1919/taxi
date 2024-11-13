package com.modsen.rating.mapper

import com.modsen.rating.dto.RequestRate
import com.modsen.rating.dto.ResponseRate
import com.modsen.rating.entity.Rate
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface RateMapper {

    fun rateToResponseRate(rate: Rate): ResponseRate
    fun requestRateToRate(requestRate: RequestRate): Rate
}