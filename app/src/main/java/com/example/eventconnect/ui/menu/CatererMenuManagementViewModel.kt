package com.example.eventconnect.ui.menu

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CatererMenuManagementViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _menu = MutableStateFlow<List<MenuResponse>>(emptyList())
    val menu: StateFlow<List<MenuResponse>> = _menu

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadMenu() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val user = FirebaseAuth.getInstance().currentUser ?: return@launch
                val token = user.getIdToken(false).await().token ?: return@launch

                val response =
                    RetrofitClient.apiService.getMyMenu("Bearer $token")

                if (response.isSuccessful) {
                    _menu.value = response.body() ?: emptyList()
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun addMenu(request: MenuCreateRequest, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser ?: return@launch
                val token = user.getIdToken(false).await().token ?: return@launch

                var imageUrl: String? = null

                if (imageUri != null) {
                    val context = getApplication<Application>()
                    val stream =
                        context.contentResolver.openInputStream(imageUri)
                    val bytes = stream!!.readBytes()

                    val requestFile =
                        bytes.toRequestBody("image/*".toMediaTypeOrNull())

                    val body = MultipartBody.Part.createFormData(
                        "file",
                        "menu.jpg",
                        requestFile
                    )

                    val uploadResponse =
                        RetrofitClient.apiService.uploadMenuImage(
                            "Bearer $token",
                            body
                        )

                    if (uploadResponse.isSuccessful) {
                        imageUrl = uploadResponse.body()?.image_url
                    }
                }

                RetrofitClient.apiService.createMenu(
                    "Bearer $token",
                    request.copy(image_url = imageUrl)
                )

                loadMenu()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteMenu(menuId: Int) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: return@launch
            val token = user.getIdToken(false).await().token ?: return@launch

            RetrofitClient.apiService.deleteMenu(
                "Bearer $token",
                menuId
            )

            loadMenu()
        }
    }
}