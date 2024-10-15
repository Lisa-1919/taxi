package com.example.rating_service.dto

data class PagedResponseRateList(
    val rates: List<ResponseRate>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)