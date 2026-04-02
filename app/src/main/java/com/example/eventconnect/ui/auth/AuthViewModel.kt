package com.example.eventconnect.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.eventconnect.data.network.FcmTokenRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.example.eventconnect.utils.getAuthHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> = _authState

    private val _resetPasswordState = MutableStateFlow<String?>(null)
    val resetPasswordState: StateFlow<String?> = _resetPasswordState

    fun login(email: String, password: String) {
        _authState.value = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = "SUCCESS"
                viewModelScope.launch {
                    delay(500)
                    saveFcmTokenAfterLogin()
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = exception.message
            }
    }

    fun signup(email: String, password: String) {
        _authState.value = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = "SUCCESS"
                viewModelScope.launch {
                    delay(500)
                    saveFcmTokenAfterLogin()
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = exception.message
            }
    }

    fun resetPassword(email: String) {
        _resetPasswordState.value = "LOADING"
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _resetPasswordState.value = "SUCCESS"
            }
            .addOnFailureListener { exception ->
                _resetPasswordState.value = exception.message
            }
    }

    fun clearResetState() {
        _resetPasswordState.value = null
    }

    // ✅ NEW: Save FCM token to backend after login/signup
    private fun saveFcmTokenAfterLogin() {
        viewModelScope.launch {
            try {
                val authHeader = getAuthHeader() ?: return@launch

                // Get FCM token
                val fcmToken = FirebaseMessaging.getInstance().token.await()

                // Save to backend
                val response = RetrofitClient.apiService.saveFcmToken(
                    authHeader,
                    FcmTokenRequest(token = fcmToken)
                )

                if (response.isSuccessful) {
                    Log.d("FCM_TOKEN", "✅ FCM Token saved successfully: $fcmToken")
                } else {
                    Log.e("FCM_TOKEN", "❌ Failed to save FCM token: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FCM_TOKEN", "❌ Error saving FCM token: ${e.message}", e)
            }
        }
    }
}
