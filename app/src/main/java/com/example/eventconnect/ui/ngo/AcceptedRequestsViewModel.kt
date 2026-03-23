package com.example.eventconnect.ui.ngo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.AcceptedRequestResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.utils.getAuthHeader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AcceptedRequestsViewModel : ViewModel() {

    private val _requests =
        MutableStateFlow<List<AcceptedRequestResponse>>(emptyList())

    val requests: StateFlow<List<AcceptedRequestResponse>> = _requests

    fun loadRequests() {

        viewModelScope.launch {

            try {

                if (FirebaseAuth.getInstance().currentUser == null) return@launch
                val authHeader = getAuthHeader() ?: return@launch

                val response =
                    RetrofitClient.apiService.getMyAcceptedRequests(
                        authHeader
                    )

                if (response.isSuccessful) {
                    _requests.value = response.body() ?: emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}