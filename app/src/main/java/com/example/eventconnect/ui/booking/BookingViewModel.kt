package com.example.eventconnect.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.BookingCreateRequest
import com.example.eventconnect.data.network.BookingItemRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookingViewModel : ViewModel() {

    fun sendBooking(
        eventId: Int,
        catererId: Int,
        attendees: Int,
        bookingDate: String,
        items: List<BookingItemRequest>
    ) {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser ?: return@launch
                val token = user.getIdToken(false).await().token ?: return@launch

                val request = BookingCreateRequest(
                    event_id = eventId,
                    caterer_id = catererId,
                    attendees = attendees,
                    booking_date = bookingDate,
                    items = items
                )

                RetrofitClient.apiService.createBooking(
                    "Bearer $token",
                    request
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}