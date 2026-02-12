package com.example.eventconnect.ui.caterer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.BookingResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatererBookingViewModel : ViewModel() {

    private val _bookings =
        MutableStateFlow<List<BookingResponse>>(emptyList())
    val bookings: StateFlow<List<BookingResponse>> = _bookings

    private val _error =
        MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadBookings() {
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                val response =
                    RetrofitClient.apiService
                        .getCatererBookings("Bearer $token")

                if (response.isSuccessful) {
                    _bookings.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load bookings"
                }

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun respondBooking(bookingId: Int, status: String) {
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                RetrofitClient.apiService.respondBooking(
                    token = "Bearer $token",
                    bookingId = bookingId,
                    status = status
                )

                loadBookings()

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
