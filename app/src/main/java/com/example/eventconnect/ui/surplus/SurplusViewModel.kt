package com.example.eventconnect.ui.surplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.SurplusNGOResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.data.network.SurplusAlertResponse
import com.example.eventconnect.data.network.SurplusAlertRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.eventconnect.utils.getAuthHeader

class SurplusViewModel : ViewModel() {

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _acceptedNgo = MutableStateFlow<SurplusNGOResponse?>(null)
    val acceptedNgo: StateFlow<SurplusNGOResponse?> = _acceptedNgo

    var requestId: Int? = null

    fun sendAlert(
        eventId: Int,
        description: String,
        imageUrl: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            try {
                if (FirebaseAuth.getInstance().currentUser == null) return@launch
                val authHeader = getAuthHeader() ?: return@launch
                val request = SurplusAlertRequest(
                    event_id = eventId,
                    description = description,
                    image_url = imageUrl.takeIf { it.isNotBlank() },
                    latitude = latitude,
                    longitude = longitude
                )
                val response = RetrofitClient.apiService.sendSurplusAlert(
                    authHeader,
                    request
                )
                if (response.isSuccessful) {
                    _success.value = true
                    requestId = response.body()?.request_id

                    startPolling()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchAcceptedNgoOnce(requestId: Int) {
        viewModelScope.launch {
            checkAcceptedNgo(requestId)
        }
    }

    private fun startPolling() {

        viewModelScope.launch {

            while (_acceptedNgo.value == null) {

                delay(5000)

                requestId?.let { checkAcceptedNgo(it) }
            }
        }
    }

    private suspend fun checkAcceptedNgo(requestId: Int) {

        try {

            if (FirebaseAuth.getInstance().currentUser == null) return
            val authHeader = getAuthHeader() ?: return

            val response =
                RetrofitClient.apiService.getAcceptedNgo(
                    authHeader,
                    requestId
                )
            if (response.isSuccessful && response.body() != null) {

                _acceptedNgo.value = response.body()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchLocation(requestId: Int, onResult: (Double, Double) -> Unit) {
        viewModelScope.launch {
            try {
                if (FirebaseAuth.getInstance().currentUser == null) return@launch
                val authHeader = getAuthHeader() ?: return@launch

                val response = RetrofitClient.apiService.getSurplusLocation(
                    authHeader,
                    requestId
                )

                if (response.isSuccessful) {
                    val body: Map<String, Double> = response.body() ?: return@launch
                    val lat = body["latitude"] ?: return@launch
                    val lng = body["longitude"] ?: return@launch

                    onResult(lat, lng)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
