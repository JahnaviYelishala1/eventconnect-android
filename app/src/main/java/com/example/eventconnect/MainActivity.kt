package com.example.eventconnect

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.eventconnect.data.network.FcmTokenRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.ui.SplashScreen
import com.example.eventconnect.ui.navigation.NavGraph
import com.example.eventconnect.ui.theme.EventconnectTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ REQUIRED FOR OPENSTREETMAP
        Configuration.getInstance().userAgentValue = packageName

        // ✅ Create notification channel for Android 8+
        createNotificationChannel()

        // ✅ Get FCM token and save to backend if user is authenticated
        getFcmTokenAndSave()

        enableEdgeToEdge()

        setContent {
            EventconnectTheme {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2000) // Show splash for 2 seconds
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    NavGraph()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "chat_channel",
                "Chat Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for chat messages and updates"
            }

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    // ✅ Updated: Save FCM token to backend if user is authenticated
    private fun getFcmTokenAndSave() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            Log.d("FCM_TOKEN", "FCM Token obtained: $fcmToken")

            // ✅ Check if user is authenticated and save token
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User is already logged in, save token to backend
                lifecycleScope.launch {
                    saveFcmTokenToBackend(fcmToken)
                }
            } else {
                Log.d("FCM_TOKEN", "User not authenticated yet, token will be saved after login")
            }
        }.addOnFailureListener { exception ->
            Log.e("FCM_TOKEN", "Failed to get FCM token: ${exception.message}", exception)
        }
    }

    // ✅ Helper function to save FCM token to backend
    private suspend fun saveFcmTokenToBackend(fcmToken: String) {
        try {
            val user = FirebaseAuth.getInstance().currentUser ?: return
            val authToken = user.getIdToken(true).await().token ?: return

            val response = RetrofitClient.apiService.saveFcmToken(
                "Bearer $authToken",
                FcmTokenRequest(token = fcmToken)
            )

            if (response.isSuccessful) {
                Log.d("FCM_TOKEN", "✅ FCM Token saved successfully on app start")
            } else {
                Log.e("FCM_TOKEN", "❌ Failed to save FCM token: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("FCM_TOKEN", "❌ Error saving FCM token: ${e.message}", e)
        }
    }
}
