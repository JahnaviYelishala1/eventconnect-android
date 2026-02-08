package com.example.eventconnect.data.network

data class EventResponse(
    val id: Int,
    val event_name: String,
    val event_type: String,
    val attendees: Int,
    val duration_hours: Int,
    val meal_style: String,
    val location_type: String,
    val season: String,
    val estimated_food_quantity: Double,
    val unit: String
)
