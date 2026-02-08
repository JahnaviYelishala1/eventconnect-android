package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.EventResponse
import com.example.eventconnect.data.network.RetrofitClient
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
}
