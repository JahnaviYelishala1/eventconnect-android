package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.EventCreateRequest
import com.example.eventconnect.data.network.FoodPredictionRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreateEventViewModel : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _prediction = MutableStateFlow<String?>(null)
    val prediction: StateFlow<String?> = _prediction

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun setError(message: String) {
        _error.value = message
    }

    fun saveEvent(
        eventName: String,
        eventType: String,
        guests: Int,
        duration: Int,
        mealStyle: String,
        locationType: String,
        season: String,
        address: String,
        city: String,
        pincode: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val currentUser = FirebaseAuth.getInstance().currentUser
                    ?: run {
                        _error.value = "User not logged in"
                        return@launch
                    }

                val token = currentUser.getIdToken(false).await().token
                    ?: run {
                        _error.value = "Failed to get auth token"
                        return@launch
                    }

                val response = RetrofitClient.apiService.createEvent(
                    "Bearer $token",
                    EventCreateRequest(
                        event_name = eventName,
                        event_type = eventType,
                        attendees = guests,
                        duration_hours = duration,
                        meal_style = mealStyle,
                        location_type = locationType,
                        season = season,
                        address = address,
                        city = city,
                        pincode = pincode,
                        latitude = latitude,
                        longitude = longitude
                    )
                )

                if (response.isSuccessful) {
                    _prediction.value = "Event saved successfully 🎉"
                } else {
                    _error.value =
                        "Failed (${response.code()}): ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }
}
