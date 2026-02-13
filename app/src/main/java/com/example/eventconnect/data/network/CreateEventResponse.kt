package com.example.eventconnect.data.network



data class CreateEventResponse(
    val id: Int,
    val event_name: String,
    val estimated_food_quantity: Double,
    val unit: String,
    val latitude: Double?,
    val longitude: Double?,
    val city: String?
)
