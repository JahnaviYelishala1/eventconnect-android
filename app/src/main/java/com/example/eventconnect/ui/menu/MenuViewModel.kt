package com.example.eventconnect.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuViewModel : ViewModel() {

    /* ---------------- MENU STATE ---------------- */

    private val _menu = MutableStateFlow<List<MenuResponse>>(emptyList())
    val menu: StateFlow<List<MenuResponse>> = _menu

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /* ---------------- BOOKING STATE ---------------- */

    private val _bookingLoading = MutableStateFlow(false)
    val bookingLoading: StateFlow<Boolean> = _bookingLoading

    private val _bookingSuccess = MutableStateFlow(false)
    val bookingSuccess: StateFlow<Boolean> = _bookingSuccess

    private val _bookingError = MutableStateFlow<String?>(null)
    val bookingError: StateFlow<String?> = _bookingError

    /* ---------------- LOAD MENU ---------------- */

    fun loadMenu(catererId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val user = FirebaseAuth.getInstance().currentUser
                    ?: throw Exception("User not logged in")

                val token = user.getIdToken(false).await().token
                    ?: throw Exception("Failed to get token")

                val response = RetrofitClient.apiService
                    .getCatererMenu("Bearer $token", catererId)

                if (response.isSuccessful) {
                    _menu.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed: ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    /* ---------------- SEND BOOKING ---------------- */

    fun sendBookingRequest(request: BookingCreateRequest) {
        viewModelScope.launch {
            try {
                _bookingLoading.value = true
                _bookingError.value = null
                _bookingSuccess.value = false

                val user = FirebaseAuth.getInstance().currentUser
                    ?: throw Exception("User not logged in")

                val token = user.getIdToken(false).await().token
                    ?: throw Exception("Failed to get token")

                val response = RetrofitClient.apiService.createBooking(
                    token = "Bearer $token",
                    request = request
                )

                if (response.isSuccessful) {
                    _bookingSuccess.value = true
                } else {
                    _bookingError.value =
                        "Booking failed: ${response.code()}"
                }

            } catch (e: Exception) {
                _bookingError.value =
                    e.localizedMessage ?: "Booking error"
            } finally {
                _bookingLoading.value = false
            }
        }
    }

    fun resetBookingState() {
        _bookingSuccess.value = false
        _bookingError.value = null
    }
}