package com.example.eventconnect.ui.menu

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.*
import com.example.eventconnect.utils.getAuthHeader
import com.example.eventconnect.utils.uriToFile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class CatererMenuManagementViewModel : ViewModel() {

    private val _menu = MutableStateFlow<List<MenuResponse>>(emptyList())
    val menu: StateFlow<List<MenuResponse>> = _menu

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _addMenuSuccess = MutableStateFlow(false)
    val addMenuSuccess: StateFlow<Boolean> = _addMenuSuccess

    fun loadMenu() {
        viewModelScope.launch {
            _loading.value = true
            try {
                if (FirebaseAuth.getInstance().currentUser == null) return@launch
                val authHeader = getAuthHeader() ?: return@launch

                val response = RetrofitClient.apiService.getMyMenu(authHeader)

                if (response.isSuccessful) {
                    _menu.value = response.body() ?: emptyList()
                    Log.d("MenuVM", "Menu loaded successfully")
                } else {
                    Log.e("MenuVM", "Failed to load menu: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MenuVM", "Error loading menu", e)
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun uploadImage(context: Context, uri: Uri, authHeader: String): String? {
        val file = uriToFile(context, uri) ?: return null
        val requestFile = file.asRequestBody("image/*".toMediaType())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val response = RetrofitClient.apiService.uploadMenuImage(authHeader, body)
        return if (response.isSuccessful) {
            response.body()?.image_url
        } else {
            Log.e("MenuVM", "Image upload failed: ${response.code()}")
            null
        }
    }

    fun addMenu(context: Context, request: MenuCreateRequest, imageUri: Uri?) {
        viewModelScope.launch {
            Log.d("MenuVM", "addMenu started")
            _loading.value = true
            _addMenuSuccess.value = false

            try {
                val authHeader = getAuthHeader() ?: return@launch

                var imageUrl: String? = null
                if (imageUri != null) {
                    try {
                        Log.d("MenuVM", "Uploading image...")
                        imageUrl = uploadImage(context, imageUri, authHeader)
                        Log.d("MenuVM", "Image uploaded: $imageUrl")
                    } catch (e: Exception) {
                        Log.e("MenuVM", "Image upload failed", e)
                    }
                }

                val finalRequest = request.copy(
                    image_url = imageUrl
                )

                Log.d("MenuVM", "Calling createMenu API")
                val response = RetrofitClient.apiService.createMenu(
                    authHeader,
                    finalRequest
                )
                Log.d("MenuVM", "CreateMenu Response: ${response.code()}")

                if (response.isSuccessful) {
                    _addMenuSuccess.value = true
                    loadMenu()
                }
            } catch (e: Exception) {
                Log.e("MenuVM", "Create menu error", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetAddMenuSuccess() {
        _addMenuSuccess.value = false
    }

    fun deleteMenu(menuId: Int) {
        viewModelScope.launch {
            if (FirebaseAuth.getInstance().currentUser == null) return@launch
            val authHeader = getAuthHeader() ?: return@launch

            try {
                val response = RetrofitClient.apiService.deleteMenu(authHeader, menuId)
                if (response.isSuccessful) {
                    Log.d("MenuVM", "Menu item deleted successfully")
                    loadMenu()
                } else {
                    Log.e("MenuVM", "Failed to delete menu item: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MenuVM", "Error deleting menu item", e)
            }
        }
    }
}
