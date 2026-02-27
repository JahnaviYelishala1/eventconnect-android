package com.example.eventconnect.data.network

data class MenuResponse(
    val id: Int,
    val item_name: String,
    val description: String?,
    val price: Double,
    val category: String?
)