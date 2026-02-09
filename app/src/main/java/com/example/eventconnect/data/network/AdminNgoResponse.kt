package com.example.eventconnect.data.network

data class AdminNgoResponse(
    val id: Int,
    val name: String,
    val registration_number: String,
    val status: String,
    val documents: List<AdminNgoDocument>
)
