package com.example.eventconnect.data.network

data class NgoProfileRequest(
    val name: String?,
    val established_year: String?,
    val about: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val image_url: String?
)