package com.example.eventconnect.data.network

data class MenuCreateRequest(
    val item_name: String,
    val description: String?,
    val price: Double,
    val category: String,
    val food_type: String,
    val image_url: String?
)