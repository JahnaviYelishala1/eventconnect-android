package com.example.eventconnect.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.BookingResponse   // ✅ REQUIRED
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Response

class OrganizerBookingsViewModel : ViewModel() {

    private val _bookings =
        MutableStateFlow<List<BookingResponse>>(emptyList())
    val bookings: StateFlow<List<BookingResponse>> = _bookings

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadBookings() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val user = FirebaseAuth.getInstance().currentUser
                    ?: throw Exception("User not logged in")

                val token = user.getIdToken(false).await().token
                    ?: throw Exception("Failed to get token")

                val response: Response<List<BookingResponse>> =
                    RetrofitClient.apiService.getOrganizerBookings(
                        "Bearer $token"
                    )

                if (response.isSuccessful) {
                    _bookings.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error: ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }
}