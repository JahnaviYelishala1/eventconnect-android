package com.example.eventconnect.ui.home

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.*
import com.example.eventconnect.utils.uriToFile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class CatererProfileViewModel : ViewModel() {

    private val _profile = MutableStateFlow<CatererProfileResponse?>(null)
    val profile: StateFlow<CatererProfileResponse?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var isEditing = mutableStateOf(false)

    fun loadProfile() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(true)
                    ?.await()
                    ?.token ?: return@launch

                val response = RetrofitClient.apiService.getCatererProfile("Bearer $token")
                when {
                    response.isSuccessful -> {
                        _profile.value = response.body()
                        Log.d("CatererProfileVM", "GET /api/caterers/profile code=${response.code()} body=${response.body()}")
                    }
                    response.code() == 404 -> {
                        _profile.value = null
                        Log.d("CatererProfileVM", "GET /api/caterers/profile returned 404 (profile not created)")
                    }
                    response.code() == 403 -> {
                        _profile.value = null
                        Log.e("CatererProfileVM", "User not caterer")
                    }
                    else -> {
                        val errorBody = response.errorBody()?.string().orEmpty()
                        Log.e("CatererProfileVM", "GET /api/caterers/profile failed code=${response.code()} error=$errorBody")
                    }
                }
            } catch (e: Exception) {
                Log.e("CatererProfileVM", "Error loading caterer profile", e)
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun uploadImage(context: Context, uri: Uri, token: String): String? {
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
            Log.e(
                "CatererProfileVM",
                "POST /api/caterers/upload-image failed code=${response.code()} error=${response.errorBody()?.string().orEmpty()}"
            )
            null
        }
    }

    fun createProfile(context: Context, request: CatererCreateRequest, imageUri: Uri?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(true)
                    ?.await()
                    ?.token ?: return@launch

                val imageUrl = if (imageUri != null) {
                    uploadImage(context, imageUri, token) ?: request.image_url
                } else {
                    request.image_url
                }

                val finalRequest = request.copy(image_url = imageUrl)
                val response = RetrofitClient.apiService.createCatererProfile("Bearer $token", finalRequest)
                if (response.isSuccessful) {
                    Log.d("CatererProfileVM", "POST /api/caterers/profile code=${response.code()} body=${response.body()}")
                    loadProfile()
                } else {
                    Log.e(
                        "CatererProfileVM",
                        "POST /api/caterers/profile failed code=${response.code()} error=${response.errorBody()?.string().orEmpty()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("CatererProfileVM", "Error creating caterer profile", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfile(context: Context, request: CatererCreateRequest, imageUri: Uri?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(true)
                    ?.await()
                    ?.token ?: return@launch

                val imageUrl = if (imageUri != null) {
                    uploadImage(context, imageUri, token) ?: request.image_url
                } else {
                    request.image_url
                }

                val finalRequest = request.copy(image_url = imageUrl)
                val response = RetrofitClient.apiService.updateCatererProfile("Bearer $token", finalRequest)
                if (response.isSuccessful) {
                    Log.d("CatererProfileVM", "PUT /api/caterers/profile code=${response.code()} body=${response.body()}")
                    isEditing.value = false
                    loadProfile()
                } else {
                    Log.e(
                        "CatererProfileVM",
                        "PUT /api/caterers/profile failed code=${response.code()} error=${response.errorBody()?.string().orEmpty()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("CatererProfileVM", "Error updating caterer profile", e)
            } finally {
                _loading.value = false
            }
        }
    }
}
