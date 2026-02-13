package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.CatererResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FindCatererViewModel : ViewModel() {

    private val _caterers =
        MutableStateFlow<List<CatererResponse>>(emptyList())
    val caterers: StateFlow<List<CatererResponse>> = _caterers

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCaterers(
        eventId: Int,
        vegOnly: Boolean? = null,
        nonVegOnly: Boolean? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        mealStyle: String? = null
    ) {
        viewModelScope.launch {

            try {
                _loading.value = true
                _error.value = null

                val user = FirebaseAuth.getInstance().currentUser
                    ?: return@launch

                val token =
                    user.getIdToken(false).await().token
                        ?: return@launch

                val response =
                    RetrofitClient.apiService.matchCaterers(
                        token = "Bearer $token",
                        eventId = eventId,
                        vegOnly = vegOnly,
                        nonVegOnly = nonVegOnly,
                        minPrice = minPrice,
                        maxPrice = maxPrice,
                        mealStyle = mealStyle
                    )

                if (response.isSuccessful) {
                    _caterers.value =
                        response.body() ?: emptyList()
                } else {
                    _error.value = "Error ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }
}
