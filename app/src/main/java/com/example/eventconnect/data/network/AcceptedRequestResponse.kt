package com.example.eventconnect.data.network

data class AcceptedRequestResponse(
    val request_id: Int,
    val event_name: String,
    val food_description: String,
    val latitude: Double,
    val longitude: Double,
    val created_at: String
)