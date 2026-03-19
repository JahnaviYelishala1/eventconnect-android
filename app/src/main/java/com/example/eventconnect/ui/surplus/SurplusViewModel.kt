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
                val user = FirebaseAuth.getInstance().currentUser!!
                val token = user.getIdToken(false).await().token!!
                val request = SurplusAlertRequest(
                    event_id = eventId,
                    description = description,
                    image_url = imageUrl.takeIf { it.isNotBlank() },
                    latitude = latitude,
                    longitude = longitude
                )
                val response = RetrofitClient.apiService.sendSurplusAlert(
                    "Bearer $token",
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

    private fun startPolling() {

        viewModelScope.launch {

            while (true) {

                delay(5000)

                requestId?.let { checkAcceptedNgo(it) }
            }
        }
    }

    private suspend fun checkAcceptedNgo(requestId: Int) {

        try {

            val user = FirebaseAuth.getInstance().currentUser!!
            val token = user.getIdToken(false).await().token!!

            val response =
                RetrofitClient.apiService.getAcceptedNgo(
                    "Bearer $token",
                    requestId
                )
            if (response.isSuccessful && response.body() != null) {

                _acceptedNgo.value = response.body()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
