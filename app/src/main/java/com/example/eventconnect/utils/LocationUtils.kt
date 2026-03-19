package com.example.eventconnect.utils

import android.content.Context
import android.location.Geocoder
import java.util.*

fun getAddressFromLatLng(
    context: Context,
    latitude: Double,
    longitude: Double
): String {

    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            address.getAddressLine(0) ?: "Address not found"
        } else {
            "Address not found"
        }

    } catch (e: Exception) {
        e.printStackTrace()
        "Unable to fetch address"
    }
}