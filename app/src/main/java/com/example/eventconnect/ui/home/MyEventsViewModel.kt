package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyEventsViewModel : ViewModel() {

    // ---------------- EVENTS ----------------
    private val _events =
        MutableStateFlow<List<EventResponse>>(emptyList())
    val events: StateFlow<List<EventResponse>> = _events

    private val _error =
        MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // =========================================================
    // LOAD EVENTS
    // =========================================================
    fun loadEvents() {

        viewModelScope.launch {

            try {
                _error.value = null

                val currentUser =
                    FirebaseAuth.getInstance().currentUser
                        ?: run {
                            _error.value = "User not logged in"
                            return@launch
                        }

                val token =
                    currentUser.getIdToken(false).await().token
                        ?: run {
                            _error.value = "Token error"
                            return@launch
                        }

                val response =
                    RetrofitClient.apiService
                        .getMyEvents("Bearer $token")

                if (response.isSuccessful) {
                    _events.value = response.body() ?: emptyList()
                } else {
                    _error.value =
                        "Failed to load events (${response.code()})"
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    // =========================================================
    // COMPLETE EVENT
    // =========================================================
    fun completeEvent(
        eventId: Int,
        foodPrepared: Double,
        foodConsumed: Double,
        surplusLocation: SurplusLocationRequest?
    ) {

        viewModelScope.launch {

            try {

                val currentUser =
                    FirebaseAuth.getInstance().currentUser
                        ?: run {
                            _error.value = "User not logged in"
                            return@launch
                        }

                val token =
                    currentUser.getIdToken(false).await().token
                        ?: run {
                            _error.value = "Token error"
                            return@launch
                        }

                val response =
                    RetrofitClient.apiService.completeEvent(
                        "Bearer $token",
                        eventId,
                        CompleteEventRequest(
                            food_prepared = foodPrepared,
                            food_consumed = foodConsumed,
                            surplus_location = surplusLocation
                        )
                    )

                if (response.isSuccessful) {
                    loadEvents() // Refresh list
                } else {
                    _error.value =
                        "Failed to complete event (${response.code()})"
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }
}
