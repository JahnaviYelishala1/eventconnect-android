package com.example.eventconnect.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.MenuCreateRequest
import com.example.eventconnect.data.network.MenuResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CatererMenuManagementViewModel : ViewModel() {

    private val _menu = MutableStateFlow<List<MenuResponse>>(emptyList())
    val menu: StateFlow<List<MenuResponse>> = _menu

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadMenu() {
        viewModelScope.launch {
            try {
                _loading.value = true

                val user = FirebaseAuth.getInstance().currentUser ?: return@launch
                val token = user.getIdToken(false).await().token ?: return@launch

                val response =
                    RetrofitClient.apiService.getMyMenu("Bearer $token")

                if (response.isSuccessful) {
                    _menu.value = response.body() ?: emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun addMenu(request: MenuCreateRequest) {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser ?: return@launch
                val token = user.getIdToken(false).await().token ?: return@launch

                RetrofitClient.apiService.createMenu(
                    "Bearer $token",
                    request
                )

                loadMenu()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteMenu(menuId: Int) {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser ?: return@launch
                val token = user.getIdToken(false).await().token ?: return@launch

                RetrofitClient.apiService.deleteMenu(
                    "Bearer $token",
                    menuId
                )

                loadMenu()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}