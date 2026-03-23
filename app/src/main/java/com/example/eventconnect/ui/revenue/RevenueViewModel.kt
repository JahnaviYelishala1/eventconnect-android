package com.example.eventconnect.ui.revenue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.RevenueResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.utils.getAuthHeader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RevenueViewModel : ViewModel() {

    private val _revenue = MutableStateFlow<RevenueResponse?>(null)
    val revenue: StateFlow<RevenueResponse?> = _revenue

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadRevenue() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                if (FirebaseAuth.getInstance().currentUser == null) return@launch
                val authHeader = getAuthHeader() ?: return@launch

                val response = RetrofitClient.apiService
                    .getCatererRevenue(authHeader)

                if (response.isSuccessful) {
                    _revenue.value = response.body()
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