package com.example.eventconnect.data.network

data class ChatMessageResponse(
    val sender_id: Int,
    val message: String,
    val timestamp: String
)