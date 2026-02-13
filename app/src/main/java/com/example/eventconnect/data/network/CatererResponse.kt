package com.example.eventconnect.data.network

data class CatererResponse(
    val id: Int,
    val business_name: String,
    val city: String,
    val min_capacity: Int,
    val max_capacity: Int,
    val price_per_plate: Double,
    val veg_supported: Boolean,
    val nonveg_supported: Boolean,
    val rating: Double,
    val latitude: Double,
    val longitude: Double,
    val image_url: String?,
    val distance_km: Double,
    val services: List<CatererServiceResponse>
)