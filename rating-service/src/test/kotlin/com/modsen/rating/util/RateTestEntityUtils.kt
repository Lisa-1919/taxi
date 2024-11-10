package com.modsen.rating.util

import com.modsen.rating.dto.PagedResponseRateList
import com.modsen.rating.dto.RequestRate
import com.modsen.rating.dto.ResponseRate
import com.modsen.rating.entity.Rate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

import org.springframework.data.domain.PageRequest
object RateTestEntityUtils {

    const val DEFAULT_RATE_ID = 1L
    private const val DEFAULT_RIDE_ID = 1L
    const val DEFAULT_USER_ID = 1L
    private const val DEFAULT_RATING = 5.0
    private const val DEFAULT_COMMENTARY = "some commentary"

    private const val DEFAULT_PAGE_NUMBER = 0
    private const val DEFAULT_PAGE_SIZE = 10
    const val DEFAULT_TOTAL_ELEMENTS = 1L
    private const val DEFAULT_TOTAL_PAGES = 1
    private const val DEFAULT_LAST = true

    fun createTestRequestRate(
        rideId: Long = DEFAULT_RIDE_ID,
        userId: Long = DEFAULT_USER_ID,
        userType: UserType = UserType.PASSENGER,
        rating: Double = DEFAULT_RATING,
        rideCommentary: String = DEFAULT_COMMENTARY
    ): RequestRate {
        return RequestRate(userId, userType, rideId, rating, rideCommentary)
    }


    fun createTestRequestRate(
    ): RequestRate {
        return RequestRate(
            userId = DEFAULT_USER_ID,
            userType = UserType.PASSENGER,
            rideId = DEFAULT_RIDE_ID,
            rate = DEFAULT_RATING,
            rideCommentary = DEFAULT_COMMENTARY
        )
    }

    fun createTestRate(): Rate {
        return Rate(
            id = DEFAULT_RATE_ID,
            userId = DEFAULT_USER_ID,
            userType = UserType.PASSENGER,
            rideId = DEFAULT_RIDE_ID,
            rate = DEFAULT_RATING,
            rideCommentary = DEFAULT_COMMENTARY
        )
    }

    fun createTestRate(
        id: Long = DEFAULT_RATE_ID,
        rideId: Long = DEFAULT_RIDE_ID,
        userId: Long = DEFAULT_USER_ID,
        userType: UserType = UserType.PASSENGER,
        rating: Double = DEFAULT_RATING,
        rideCommentary: String = DEFAULT_COMMENTARY
    ): Rate {
        return Rate(id,userId, userType, rideId,  rating, rideCommentary)
    }

    fun createTestResponseRate(
        id: Long = DEFAULT_RIDE_ID,
        rideId: Long = DEFAULT_RIDE_ID,
        userId: Long = DEFAULT_USER_ID,
        userType: UserType = UserType.PASSENGER,
        rating: Double = DEFAULT_RATING,
        rideCommentary: String = DEFAULT_COMMENTARY
    ): ResponseRate {
        return ResponseRate(id,  userId, userType, rideId, rating, rideCommentary)
    }

    fun createPageRequest(): PageRequest {
        return PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE)
    }

    fun createDefaultRatePage(rates: List<Rate>?): Page<Rate> {
        return PageImpl<Rate>(rates, createPageRequest(), DEFAULT_TOTAL_ELEMENTS)
    }

    fun createPagedResponseRateList(
        list: List<ResponseRate>,
        pageNumber: Int = DEFAULT_PAGE_NUMBER,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        totalElements: Long = DEFAULT_TOTAL_ELEMENTS,
        totalPages: Int = DEFAULT_TOTAL_PAGES,
        last: Boolean = DEFAULT_LAST

    ): PagedResponseRateList{
        return PagedResponseRateList(list, pageNumber, pageSize, totalElements, totalPages, last)
    }
}