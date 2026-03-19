package com.example.eventconnect.data.network

data class AcceptedRequestResponse(
    val request_id: Int,
    val event_id: Int,
    val organizer_id: Int,
    val event_name: String,
    val food_description: String,
    val image_url: String?,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val accepted_by_ngo: Int?,
    val organizer_name: String,   // ✅ NEW
    val organizer_phone: String,
    val created_at: String
)