package com.modsen.rating.dto

data class PagedResponseRateList(
    val list: List<ResponseRate>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)