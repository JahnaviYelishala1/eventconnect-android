package com.example.eventconnect.data.network

data class CompleteEventRequest(
    val food_prepared: Double,
    val food_consumed: Double,
    val surplus_location: SurplusLocationRequest? = null
)
