package com.example.eventconnect.data.network

data class EventBookingStatusResponse(
    val status: String,
    val caterer_name: String?,
    val price_per_plate: Double?,
    val caterer_phone: String?
)

