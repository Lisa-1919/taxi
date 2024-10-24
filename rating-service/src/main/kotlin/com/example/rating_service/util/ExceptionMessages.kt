package com.example.rating_service.util

enum class ExceptionMessages(private val message: String) {

    RATE_NOT_FOUND("Rate with id %s not found");

    fun format(vararg args: Any): String {
        return String.format(this.message, *args)
    }
}