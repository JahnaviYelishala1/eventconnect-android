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

class CreateEventViewModel : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _prediction = MutableStateFlow<String?>(null)
    val prediction: StateFlow<String?> = _prediction

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // ------------------ STEP 1: PREDICT FOOD ------------------
    fun estimateFood(
        eventType: String,
        guests: String,
        duration: String,
        mealStyle: String,
        location: String,
        season: String
    ) {
        if (
            eventType.isBlank() || guests.isBlank() || duration.isBlank() ||
            mealStyle.isBlank() || location.isBlank() || season.isBlank()
        ) {
            _error.value = "Please fill all fields"
            return
        }

        val guestsInt = guests.toIntOrNull()
        val durationInt = duration.toIntOrNull()

        if (guestsInt == null || durationInt == null) {
            _error.value = "Guests and duration must be numbers"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: run {
                    _error.value = "User not authenticated"
                    return@launch
                }

                val response = RetrofitClient.apiService.predictFood(
                    token = "Bearer $token",
                    request = FoodPredictionRequest(
                        event_type = eventType,
                        attendees = guestsInt,
                        duration_hours = durationInt,
                        meal_style = mealStyle,
                        location_type = location,
                        season = season
                    )
                )

                if (response.isSuccessful) {
                    val result = response.body()
                    _prediction.value =
                        "Estimated food required: ${result?.estimated_food_quantity} ${result?.unit}"
                } else {
                    _error.value = "Prediction failed (${response.code()})"
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // ------------------ STEP 2: SAVE EVENT (DB INSERT) ------------------
    fun saveEvent(
        eventName: String,
        eventType: String,
        guests: Int,
        duration: Int,
        mealStyle: String,
        location: String,
        season: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: run {
                    _error.value = "User not authenticated"
                    return@launch
                }

                val response = RetrofitClient.apiService.createEvent(
                    token = "Bearer $token",
                    request = EventCreateRequest(
                        event_name = eventName,
                        event_type = eventType,
                        attendees = guests,
                        duration_hours = duration,
                        meal_style = mealStyle,
                        location_type = location,
                        season = season
                    )
                )

                if (response.isSuccessful) {
                    _prediction.value =
                        "Event saved successfully 🎉"
                } else {
                    _error.value = "Failed to save event (${response.code()})"
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
