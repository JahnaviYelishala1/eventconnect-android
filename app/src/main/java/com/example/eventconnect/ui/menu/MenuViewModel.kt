package com.example.eventconnect.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.MenuResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuViewModel : ViewModel() {

    private val _menu = MutableStateFlow<List<MenuResponse>>(emptyList())
    val menu: StateFlow<List<MenuResponse>> = _menu

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadMenu(catererId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val user = FirebaseAuth.getInstance().currentUser ?: run {
                    _error.value = "User not logged in"
                    return@launch
                }
                val token = user.getIdToken(false).await().token ?: run {
                    _error.value = "Failed to get auth token"
                    return@launch
                }

                val response = RetrofitClient.apiService
                    .getCatererMenu("Bearer $token", catererId)

                if (response.isSuccessful) {
                    _menu.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load menu: ${response.code()} ${response.message()}"
                    _menu.value = emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.localizedMessage ?: "Unknown error occurred"
                _menu.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}