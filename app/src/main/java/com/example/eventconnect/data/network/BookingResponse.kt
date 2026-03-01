package com.example.eventconnect.data.network

data class BookingResponse(
    val id: Int,
    val event_id: Int,
    val caterer_id: Int,
    val attendees: Int?,
    val booking_date: String?,
    val status: String,
    val total_price: Double,
    val caterer_name: String?,
    val event_name: String?,
    val items: List<BookingItemDetail> = emptyList()
)

data class BookingItemDetail(
    val menu_id: Int,
    val item_name: String,
    val quantity: Int,
    val price: Double
)