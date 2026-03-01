package com.example.eventconnect.data.network

data class BookingCreateRequest(
    val event_id: Int,
    val caterer_id: Int,
    val items: List<BookingItemRequest>,
    val attendees: Int,
    val booking_date: String
)