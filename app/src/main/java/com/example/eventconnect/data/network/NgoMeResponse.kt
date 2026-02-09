package com.example.eventconnect.data.network


data class NgoMeResponse(
    val exists: Boolean,
    val ngo_id: Int? = null,
    val status: String? = null,
    val documents_uploaded: Boolean = false
)