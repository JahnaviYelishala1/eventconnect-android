package com.example.eventconnect.data.network

data class FoodPredictionRequest(
    val event_type: String,
    val attendees: Int,
    val duration_hours: Int,
    val meal_style: String,
    val location_type: String,
    val season: String
)
