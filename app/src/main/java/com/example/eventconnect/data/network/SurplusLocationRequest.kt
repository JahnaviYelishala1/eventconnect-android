package com.example.eventconnect.data.network

data class SurplusLocationRequest(
    val address: String,
    val city: String,
    val pincode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val location_type: String
)
