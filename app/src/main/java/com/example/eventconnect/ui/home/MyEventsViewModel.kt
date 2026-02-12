package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyEventsViewModel : ViewModel() {

    private val _events =
        MutableStateFlow<List<EventResponse>>(emptyList())
    val events: StateFlow<List<EventResponse>> = _events

    private val _error =
        MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // 🔥 Booking Info Map (eventId → booking data)
    private val _bookingInfo =
        MutableStateFlow<Map<Int, EventBookingStatusResponse>>(emptyMap())
    val bookingInfo:
            StateFlow<Map<Int, EventBookingStatusResponse>> = _bookingInfo

    fun loadEvents() {
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                val response =
                    RetrofitClient.apiService
                        .getMyEvents("Bearer $token")

                if (response.isSuccessful) {
                    val eventList =
                        response.body() ?: emptyList()

                    _events.value = eventList

                    // 🔥 Fetch booking status for each event
                    eventList.forEach { event ->
                        fetchBookingStatus(event.id)
                    }

                } else {
                    _error.value = "Failed to load events"
                }

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun fetchBookingStatus(eventId: Int) {
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                val res =
                    RetrofitClient.apiService
                        .getEventBookingStatus(
                            "Bearer $token",
                            eventId
                        )

                if (res.isSuccessful && res.body() != null) {
                    val currentMap =
                        _bookingInfo.value.toMutableMap()

                    currentMap[eventId] = res.body()!!

                    _bookingInfo.value = currentMap
                }

            } catch (_: Exception) { }
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
