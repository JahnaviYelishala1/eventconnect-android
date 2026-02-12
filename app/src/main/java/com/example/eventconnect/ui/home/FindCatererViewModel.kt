package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.CatererResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FindCatererViewModel : ViewModel() {

    private val _caterers = MutableStateFlow<List<CatererResponse>>(emptyList())
    val caterers: StateFlow<List<CatererResponse>> = _caterers

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadCaterers(eventId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                val response = RetrofitClient.apiService
                    .getMatchingCaterers(
                        "Bearer $token",
                        eventId
                    )

                if (response.isSuccessful) {
                    _caterers.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load caterers"
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun bookCaterer(
        eventId: Int,
        catererId: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                val response = RetrofitClient.apiService
                    .bookCaterer(
                        "Bearer $token",
                        eventId,
                        catererId
                    )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _error.value = "Booking failed"
                }

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
