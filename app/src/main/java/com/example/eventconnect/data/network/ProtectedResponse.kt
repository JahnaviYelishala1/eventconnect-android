package com.example.eventconnect.data.network

data class ProtectedResponse(
    val uid: String,
    val email: String?,
    val role: String?,
    val name: String?,
    val phone: String?
)