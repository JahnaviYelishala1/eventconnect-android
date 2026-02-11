package com.example.eventconnect.data.network

import com.google.gson.annotations.SerializedName

data class NgoProfile(
    val name: String,
    @SerializedName("established_year")
    val establishedYear: String?,
    val about: String,          // ✅ THIS WAS MISSING OR MISMATCHED
    val email: String,
    val phone: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("image_url")
    val imageUrl: String?
)