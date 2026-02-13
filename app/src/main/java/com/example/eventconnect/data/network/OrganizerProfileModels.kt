package com.example.eventconnect.data.network

// 🔹 Request body for creating/updating organizer profile
data class OrganizerProfileRequest(
    val full_name: String,
    val organization_name: String,
    val phone: String,
    val city: String,
    val profile_image_url: String? = null   // ✅ Added
)


// 🔹 Response returned from backend
data class OrganizerProfileResponse(
    val id: Int,
    val full_name: String,
    val organization_name: String,
    val phone: String,
    val city: String,
    val profile_image_url: String?          // ✅ Added
)
