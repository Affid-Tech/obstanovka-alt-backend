package com.example.facilities.api.dto

data class ErrorResponse(
    val message: String,
    val errors: List<String> = emptyList()
)
