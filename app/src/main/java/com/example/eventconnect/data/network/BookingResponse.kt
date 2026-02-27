package com.example.eventconnect.data.network

data class BookingResponse(
    val id: Int,
    val event_id: Int,
    val caterer_id: Int,
    val status: String,
    val total_price: Double
)
