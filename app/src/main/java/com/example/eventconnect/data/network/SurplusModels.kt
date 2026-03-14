package com.example.eventconnect.data.network

class SurplusCreateRequest(
    val event_id: Int,
    val food_description: String,
    val image_url: String,
    val latitude: Double,
    val longitude: Double
)

class SurplusResponse(
    val id: Int,
    val event_id: Int,
    val food_description: String,
    val image_url: String,
    val latitude: Double,
    val longitude: Double,
    val status: String
)

class SurplusNGOResponse(
    val ngo_name: String,
    val contact_person: String?,
    val phone: String
)