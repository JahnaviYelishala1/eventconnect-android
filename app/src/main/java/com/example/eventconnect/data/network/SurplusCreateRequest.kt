package com.example.eventconnect.data.network

data class SurplusCreateRequest(
    val event_id: Int,
    val food_description: String,
    val image_url: String?,
    val latitude: Double,
    val longitude: Double
)

