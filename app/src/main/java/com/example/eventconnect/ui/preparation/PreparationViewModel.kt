package com.example.eventconnect.ui.preparation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.utils.getAuthHeader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PreparationViewModel(
    private val bookingId: Int
) : ViewModel() {

    private val _status = MutableStateFlow("pending")
    val status: StateFlow<String> = _status

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStatus() {
        viewModelScope.launch {
            try {
                if (FirebaseAuth.getInstance().currentUser == null) return@launch
                val authHeader = getAuthHeader() ?: return@launch

                val response = RetrofitClient.apiService
                    .getPreparationStatus(authHeader, bookingId)

                if (response.isSuccessful) {
                    _status.value = response.body()?.status ?: "pending"
                } else {
                    Log.e("PREP", "Load status failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PREP", "Exception in loadStatus: ${e.message}", e)
            }
        }
    }

    fun updateStatus(newStatus: String) {
        viewModelScope.launch {
            try {
                _error.value = null
                if (FirebaseAuth.getInstance().currentUser == null) {
                    _error.value = "User not logged in"
                    return@launch
                }
                
                val authHeader = getAuthHeader()
                if (authHeader == null) {
                    _error.value = "Failed to get auth header"
                    return@launch
                }

                // 1. Call API and store response
                val response = RetrofitClient.apiService
                    .updatePreparationStatus(
                        authHeader,
                        bookingId,
                        newStatus
                    )

                // 2. Only update UI if success
                if (response.isSuccessful) {
                    _status.value = newStatus
                    Log.d("PREP", "Status updated successfully to: $newStatus")
                } else {
                    // 4. Do NOT update state blindly
                    val errorMsg = "Update failed: ${response.code()}"
                    Log.e("PREP", errorMsg)
                    _error.value = "Failed to update status. Please try again."
                }
            } catch (e: Exception) {
                // 3. Add try-catch logging
                Log.e("PREP", "Full exception message: ${e.message}", e)
                _error.value = "Network error occurred"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
