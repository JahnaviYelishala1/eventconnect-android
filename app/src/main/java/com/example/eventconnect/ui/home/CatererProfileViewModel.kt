package com.example.eventconnect.ui.caterer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.CatererProfileRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatererProfileViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun createProfile(request: CatererProfileRequest) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.result
                    ?.token ?: return@launch

                val response =
                    RetrofitClient.apiService
                        .createCatererProfile(
                            "Bearer $token",
                            request
                        )

                if (response.isSuccessful) {
                    _success.value = true
                } else {
                    _error.value = "Failed to create profile"
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
