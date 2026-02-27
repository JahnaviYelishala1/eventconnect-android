package com.example.eventconnect.data.network

data class BookingItemRequest(
    val menu_id: Int,
    val quantity: Int
)

data class BookingCreateRequest(
    val event_id: Int,
    val caterer_id: Int,
    val items: List<BookingItemRequest>
)