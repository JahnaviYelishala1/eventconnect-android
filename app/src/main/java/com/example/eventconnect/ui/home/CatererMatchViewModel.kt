package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.CatererResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatererMatchViewModel : ViewModel() {

    private val _caterers = MutableStateFlow<List<CatererResponse>>(emptyList())
    val caterers: StateFlow<List<CatererResponse>> = _caterers

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadMatches(
        eventId: Int,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Double? = null,
        vegOnly: Boolean? = null,
        nonVegOnly: Boolean? = null,
        sortBy: String? = "distance"
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser?.getIdToken(false)?.result?.token ?: return@launch

                val response =
                    RetrofitClient.apiService.getMatchingCaterers(
                        "Bearer $token",
                        eventId,
                        minPrice,
                        maxPrice,
                        minRating,
                        vegOnly,
                        nonVegOnly,
                        sortBy
                    )

                if (response.isSuccessful) {
                    _caterers.value = response.body() ?: emptyList()
                }
            } finally {
                _loading.value = false
            }
        }
    }
}
