package com.example.eventconnect.utils

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
fun fetchCurrentLocation(
    context: Context,
    onSuccess: (Double, Double) -> Unit,
    onFailure: () -> Unit
) {
    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(location.latitude, location.longitude)
            } else {
                onFailure()
            }
        }
        .addOnFailureListener {
            onFailure()
        }
}
