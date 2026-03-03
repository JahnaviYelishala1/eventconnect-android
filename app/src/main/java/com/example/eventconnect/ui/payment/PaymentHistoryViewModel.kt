package com.example.eventconnect.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.PaymentHistoryResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PaymentHistoryViewModel : ViewModel() {

    private val _payments = MutableStateFlow<List<PaymentHistoryResponse>>(emptyList())
    val payments: StateFlow<List<PaymentHistoryResponse>> = _payments

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPayments() {
        viewModelScope.launch {
            try {
                _loading.value = true

                val user = FirebaseAuth.getInstance().currentUser!!
                val token = user.getIdToken(false).await().token!!

                val response = RetrofitClient.apiService
                    .getCatererPaymentHistory("Bearer $token")

                if (response.isSuccessful) {
                    _payments.value = response.body() ?: emptyList()
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