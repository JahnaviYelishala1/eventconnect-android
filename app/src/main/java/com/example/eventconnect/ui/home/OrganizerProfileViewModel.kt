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

class OrganizerProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _profile = MutableStateFlow<OrganizerProfileResponse?>(null)
    val profile: StateFlow<OrganizerProfileResponse?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var isEditing = mutableStateOf(false)

    fun loadProfile() {
        viewModelScope.launch {
            _loading.value = true

            val token = FirebaseAuth.getInstance().currentUser
                ?.getIdToken(false)?.result?.token ?: return@launch

            val response = RetrofitClient.apiService.getOrganizerProfile("Bearer $token")

            if (response.isSuccessful) {
                _profile.value = response.body()
            }

            _loading.value = false
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

        val response =
            RetrofitClient.apiService.uploadOrganizerImage("Bearer $token", part)

        return if (response.isSuccessful) {
            response.body()?.image_url
        } else null
    }

    fun createProfile(request: OrganizerProfileRequest, imageUri: Uri?) {
        viewModelScope.launch {
            val token = FirebaseAuth.getInstance().currentUser
                ?.getIdToken(false)?.result?.token ?: return@launch

            val imageUrl = if (imageUri != null) {
                uploadImage(imageUri, token) ?: request.profile_image_url
            } else request.profile_image_url

            val finalRequest = request.copy(profile_image_url = imageUrl)

            RetrofitClient.apiService.createOrganizerProfile("Bearer $token", finalRequest)
            loadProfile()
        }
    }

    fun updateProfile(request: OrganizerProfileRequest, imageUri: Uri?) {
        viewModelScope.launch {
            val token = FirebaseAuth.getInstance().currentUser
                ?.getIdToken(false)?.result?.token ?: return@launch

            val imageUrl = if (imageUri != null) {
                uploadImage(imageUri, token) ?: request.profile_image_url
            } else request.profile_image_url

            val finalRequest = request.copy(profile_image_url = imageUrl)

            RetrofitClient.apiService.updateOrganizerProfile("Bearer $token", finalRequest)
            isEditing.value = false
            loadProfile()
        }
    }
}
