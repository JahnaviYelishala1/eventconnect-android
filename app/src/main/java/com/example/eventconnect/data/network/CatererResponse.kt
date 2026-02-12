package com.example.eventconnect.data.network

data class CatererResponse(
    val id: Int,
    val business_name: String,
    val city: String,
    val price_per_plate: Double,
    val rating: Double
)