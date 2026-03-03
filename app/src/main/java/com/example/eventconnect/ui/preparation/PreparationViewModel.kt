package com.example.eventconnect.ui.preparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PreparationViewModel(
    private val bookingId: Int
) : ViewModel() {

    private val _status = MutableStateFlow("pending")
    val status: StateFlow<String> = _status

    fun loadStatus() {
        viewModelScope.launch {

            val user = FirebaseAuth.getInstance().currentUser!!
            val token = user.getIdToken(false).await().token!!

            val response = RetrofitClient.apiService
                .getPreparationStatus("Bearer $token", bookingId)

            if (response.isSuccessful) {
                _status.value = response.body()?.status ?: "pending"
            }
        }
    }

    fun updateStatus(newStatus: String) {
        viewModelScope.launch {

            val user = FirebaseAuth.getInstance().currentUser!!
            val token = user.getIdToken(false).await().token!!

            RetrofitClient.apiService
                .updatePreparationStatus(
                    "Bearer $token",
                    bookingId,
                    newStatus
                )

            _status.value = newStatus
        }
    }
}