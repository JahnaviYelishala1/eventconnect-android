package com.example.eventconnect.ui.booking

import android.util.Log
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
                Log.d("PREDICT", "Booking fetched")

                // 2️⃣ Get Event Details (with fallback)
                val eventResponse =
                    RetrofitClient.apiService.getEventDetails(
                        authHeader,
                        booking.event_id
                    )

                val mealType = if (eventResponse.isSuccessful) {
                    Log.d("PREDICT", "Event fetched")
                    eventResponse.body()?.meal_style ?: "Buffet"
                } else {
                    Log.e("PREDICT", "Event fetch failed, using fallback")
                    "Buffet"
                }

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

                Log.d("PREDICT", "Menu size = ${menuItems.size}")

                // Map menu items by id for robust lookup
                val menuMap = menuItems.associateBy { it.id }

                // Helper to normalize category
                fun normalizeCategory(category: String?): String {
                    return when (category?.trim()?.lowercase()) {
                        "starter" -> "starter"
                        "main course", "main", "maincourse" -> "main_course"
                        "dessert", "sweet" -> "dessert"
                        "beverage", "beverages", "drink" -> "beverages"
                        "snack", "snacks" -> "snacks"
                        else -> "main_course"
                    }
                }

                // Helper to normalize food_type
                fun normalizeFoodType(foodType: String?): String {
                    return when (foodType?.trim()?.lowercase()) {
                        "veg", "vegetarian" -> "veg"
                        "nonveg", "non-veg", "non vegetarian", "nonvegetarian" -> "non-veg"
                        else -> "veg"
                    }
                }

                // 4️⃣ Prepare prediction items using menu_id
                val predictionItems = booking.items.map { item ->
                    val menu = menuMap[item.menu_id]
                    MenuItemPrediction(
                        name = item.item_name,
                        category = normalizeCategory(menu?.category),
                        food_type = normalizeFoodType(menu?.food_type)
                    )
                }

                // 5️⃣ Build request
                val request = FoodPredictionRequest(
                    attendees = booking.attendees ?: 0,
                    meal_type = mealType,
                    items = predictionItems
                )

                // 6️⃣ Call prediction API
                Log.d("PREDICT", "Calling prediction API")
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
