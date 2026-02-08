package com.example.eventconnect.data.network

data class EventCreateRequest(
    val event_name: String,
    val event_type: String,
    val attendees: Int,
    val duration_hours: Int,
    val meal_style: String,
    val location_type: String,
    val season: String
)
