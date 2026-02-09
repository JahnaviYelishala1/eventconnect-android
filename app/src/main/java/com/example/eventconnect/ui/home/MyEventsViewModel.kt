package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.CompleteEventRequest
import com.example.eventconnect.data.network.EventResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.data.network.SurplusLocationRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyEventsViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<EventResponse>>(emptyList())
    val events: StateFlow<List<EventResponse>> = _events

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadEvents() {
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                val response = RetrofitClient.apiService.getMyEvents(
                    token = "Bearer $token"
                )

                if (response.isSuccessful) {
                    _events.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load events"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun completeEvent(
        eventId: Int,
        foodPrepared: Double,
        foodConsumed: Double,
        surplusLocation: SurplusLocationRequest?
    ) {
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                val response = RetrofitClient.apiService.completeEvent(
                    token = "Bearer $token",
                    eventId = eventId,
                    request = CompleteEventRequest(
                        food_prepared = foodPrepared,
                        food_consumed = foodConsumed,
                        surplus_location = surplusLocation
                    )
                )

                if (response.isSuccessful) {
                    loadEvents()
                } else {
                    _error.value = "Failed to complete event"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
