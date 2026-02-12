package com.example.eventconnect.data.network

data class EventBookingStatusResponse(
    val event_id: Int,
    val status: String,
    val caterer_name: String?
)
