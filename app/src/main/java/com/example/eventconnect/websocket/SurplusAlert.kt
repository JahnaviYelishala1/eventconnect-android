package com.example.eventconnect.websocket

data class SurplusAlert(
    val requestId: Int,
    val eventName: String,
    val foodDescription: String,
    val imageUrl: String?,
    val distance: Double,
    val latitude: Double,
    val longitude: Double
)