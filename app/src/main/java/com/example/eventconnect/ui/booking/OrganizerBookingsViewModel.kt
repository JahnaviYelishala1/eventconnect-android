package com.example.eventconnect.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.BookingResponse
import com.example.eventconnect.data.network.PaymentResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.websocket.WebSocketManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import retrofit2.Response

class OrganizerBookingsViewModel : ViewModel() {

    private val _bookings =
        MutableStateFlow<List<BookingResponse>>(emptyList())
    val bookings: StateFlow<List<BookingResponse>> = _bookings

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var socketManager: WebSocketManager? = null

    init {
        connectSocket()
    }

    private fun connectSocket() {
        val firebaseUid =
            FirebaseAuth.getInstance().currentUser?.uid ?: return

        socketManager = WebSocketManager(firebaseUid) { message ->
            val json = JSONObject(message)
            val type = json.getString("type")

            when (type) {
                "booking_updated",
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
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelBooking(bookingId: Int) {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser!!
                val token = user.getIdToken(false).await().token!!

                RetrofitClient.apiService.updateBookingStatus(
                    "Bearer $token",
                    bookingId,
                    "cancelled"
                )

                loadBookings()

            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    // 🔥 NEW: Create Stripe Checkout Session
    fun createPaymentSession(
        bookingId: Int,
        onUrlReady: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser!!
                val token = user.getIdToken(false).await().token!!

                val response = RetrofitClient.apiService
                    .createCheckoutSession("Bearer $token", bookingId)

                if (response.isSuccessful) {
                    response.body()?.checkout_url?.let {
                        onUrlReady(it)
                    }
                } else {
                    _error.value = "Payment session failed"
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
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