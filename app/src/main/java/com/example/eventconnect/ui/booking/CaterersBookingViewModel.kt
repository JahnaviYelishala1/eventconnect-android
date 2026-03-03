package com.example.eventconnect.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.*
import com.example.eventconnect.websocket.BookingWebSocketManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class CatererBookingsViewModel : ViewModel() {

    private val _bookings =
        MutableStateFlow<List<BookingResponse>>(emptyList())
    val bookings: StateFlow<List<BookingResponse>> = _bookings

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var socketManager: BookingWebSocketManager? = null

    init {
        connectSocket()
    }

    // 🔵 WebSocket Connection
    private fun connectSocket() {

        val firebaseUid =
            FirebaseAuth.getInstance().currentUser?.uid ?: return

        socketManager = BookingWebSocketManager(firebaseUid) { message ->

            val json = JSONObject(message)
            val type = json.getString("type")

            when (type) {
                "new_booking",
                "booking_cancelled" -> {
                    loadBookings()
                }
            }
        }

        socketManager?.connect()
    }

    fun loadBookings() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val user = FirebaseAuth.getInstance().currentUser!!
                val token = user.getIdToken(false).await().token!!

                val response =
                    RetrofitClient.apiService.getCatererBookings(
                        "Bearer $token"
                    )

                if (response.isSuccessful) {
                    _bookings.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load bookings"
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateStatus(bookingId: Int, status: String) {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser!!
                val token = user.getIdToken(false).await().token!!

                RetrofitClient.apiService.updateBookingStatus(
                    "Bearer $token",
                    bookingId,
                    status
                )

                loadBookings()

            } catch (_: Exception) {}
        }
    }

    fun getPaymentDetails(
        bookingId: Int,
        onResult: (PaymentResponse?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser!!
                val token = user.getIdToken(false).await().token!!

                val response = RetrofitClient.apiService
                    .getPaymentDetails("Bearer $token", bookingId)

                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }

            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketManager?.disconnect()
    }
}
