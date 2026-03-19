package com.example.eventconnect.data.network

data class SurplusAlertRequest(
    val event_id: Int,
    val description: String,
    val image_url: String?,
    val latitude: Double,
    val longitude: Double
)

