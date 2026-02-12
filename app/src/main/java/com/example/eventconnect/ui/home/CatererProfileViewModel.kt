package com.example.eventconnect.ui.home

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.*
import com.example.eventconnect.utils.uriToFile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class CatererProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _profile = MutableStateFlow<CatererProfileResponse?>(null)
    val profile: StateFlow<CatererProfileResponse?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var isEditing = mutableStateOf(false)

    fun loadProfile() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val token = FirebaseAuth.getInstance()
                    .currentUser?.getIdToken(false)?.result?.token ?: return@launch

                val response =
                    RetrofitClient.apiService
                        .getCatererProfile("Bearer $token")

                if (response.isSuccessful) {
                    _profile.value = response.body()
                }

            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun uploadImage(uri: Uri, token: String): String? {
        val context = getApplication<Application>().applicationContext
        val file = uriToFile(context, uri) ?: return null

        val part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("image/*".toMediaType())
        )

        val response = RetrofitClient.apiService.uploadCatererImage("Bearer $token", part)
        return if (response.isSuccessful) {
            response.body()?.image_url
        } else {
            null
        }
    }

    fun createProfile(request: CatererCreateRequest, imageUri: Uri?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.result?.token
                    ?: return@launch

                val imageUrl = if (imageUri != null) {
                    uploadImage(imageUri, token) ?: request.image_url
                } else {
                    request.image_url
                }

                val finalRequest = request.copy(image_url = imageUrl)
                RetrofitClient.apiService.createCatererProfile("Bearer $token", finalRequest)
                loadProfile()
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfile(request: CatererCreateRequest, imageUri: Uri?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.result?.token
                    ?: return@launch

                val imageUrl = if (imageUri != null) {
                    uploadImage(imageUri, token) ?: request.image_url
                } else {
                    request.image_url
                }

                val finalRequest = request.copy(image_url = imageUrl)
                RetrofitClient.apiService.updateCatererProfile("Bearer $token", finalRequest)
                isEditing.value = false
                loadProfile()
            } finally {
                _loading.value = false
            }
        }
    }
}
