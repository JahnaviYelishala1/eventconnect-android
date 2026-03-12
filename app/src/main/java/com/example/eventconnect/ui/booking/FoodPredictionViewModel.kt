package com.example.eventconnect.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.FoodPredictionRequest
import com.example.eventconnect.data.network.MenuItemPrediction
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FoodPredictionViewModel : ViewModel() {

    private val _predictions =
        MutableStateFlow<Map<String, Double>>(emptyMap())

    val predictions: StateFlow<Map<String, Double>> = _predictions

    private val _loading =
        MutableStateFlow(false)

    val loading: StateFlow<Boolean> = _loading

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> = _error


    fun predictFood(bookingId: Int) {

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                val user = FirebaseAuth.getInstance().currentUser
                    ?: return@launch

                val token = user.getIdToken(false).await().token!!
                val authHeader = "Bearer $token"

                // 1️⃣ Get Booking Details
                val bookingResponse =
                    RetrofitClient.apiService.getBookingDetails(
                        authHeader,
                        bookingId
                    )

                if (!bookingResponse.isSuccessful) {
                    _error.value = "Failed to fetch booking details"
                    return@launch
                }

                val booking = bookingResponse.body()!!

                // 2️⃣ Get Event Details
                val eventResponse =
                    RetrofitClient.apiService.getEventDetails(
                        authHeader,
                        booking.event_id
                    )

                if (!eventResponse.isSuccessful) {
                    _error.value = "Failed to fetch event details"
                    return@launch
                }

                val event = eventResponse.body()!!

                // 3️⃣ Get Caterer Menu (to fetch category + food_type)
                val menuResponse =
                    RetrofitClient.apiService.getCatererMenu(
                        authHeader,
                        booking.caterer_id
                    )

                val menuItems =
                    if (menuResponse.isSuccessful)
                        menuResponse.body() ?: emptyList()
                    else emptyList()

                // Map menu items by name
                val menuMap = menuItems.associateBy { it.item_name }

                // 4️⃣ Prepare prediction items
                val predictionItems = booking.items.map { item ->

                    val menu = menuMap[item.item_name]

                    MenuItemPrediction(
                        name = item.item_name,
                        category = menu?.category ?: "main",
                        food_type = menu?.food_type ?: "veg"
                    )
                }

                // 5️⃣ Build request
                val request = FoodPredictionRequest(
                    attendees = booking.attendees ?: 0,
                    meal_type = event.meal_style,
                    items = predictionItems
                )

                // 6️⃣ Call prediction API
                val response =
                    RetrofitClient.apiService.predictFood(
                        authHeader,
                        request
                    )

                if (response.isSuccessful) {

                    _predictions.value =
                        response.body()?.predictions ?: emptyMap()

                } else {

                    _error.value = "Prediction failed"

                }

            } catch (e: Exception) {

                _error.value = e.localizedMessage

            } finally {

                _loading.value = false

            }
        }
    }
}