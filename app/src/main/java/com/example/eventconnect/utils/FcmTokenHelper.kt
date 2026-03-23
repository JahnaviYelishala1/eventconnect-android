package com.example.eventconnect.utils

import android.content.Context
import android.util.Log
import com.example.eventconnect.data.network.FcmTokenRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Save FCM token to backend after user authentication
 */
suspend fun saveFcmTokenToBackend(context: Context) {
    try {
        if (FirebaseAuth.getInstance().currentUser == null) return
        val authHeader = getAuthHeader() ?: return

        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            // This would be called from a coroutine scope
            Log.d("FCM", "FCM Token: $fcmToken - Ready to save to backend with $authHeader")
        }
    } catch (e: Exception) {
        Log.e("FCM", "Error getting FCM token", e)
    }
}

/**
 * Alternative function that uses coroutines properly
 */
suspend fun saveFcmTokenToBackendAsync(context: Context) {
    try {
        if (FirebaseAuth.getInstance().currentUser == null) return
        val authHeader = getAuthHeader() ?: return

        // Get FCM token
        val fcmToken = FirebaseMessaging.getInstance().token.await()

        // Save to backend
        val response = RetrofitClient.apiService.saveFcmToken(
            authHeader,
            FcmTokenRequest(token = fcmToken)
        )

        if (response.isSuccessful) {
            Log.d("FCM", "FCM Token saved successfully: $fcmToken")
        } else {
            Log.e("FCM", "Failed to save FCM token: ${response.errorBody()}")
        }
    } catch (e: Exception) {
        Log.e("FCM", "Error saving FCM token", e)
    }
}
