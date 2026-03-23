package com.example.eventconnect.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.ChatMessageResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.websocket.ChatWebSocketManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class ChatViewModel(
    private val requestId: Int
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageResponse>>(emptyList())
    val messages: StateFlow<List<ChatMessageResponse>> = _messages

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId

    private val _currentUserRole = MutableStateFlow<String?>(null)
    val currentUserRole: StateFlow<String?> = _currentUserRole

    private var socketManager: ChatWebSocketManager? = null

    fun loadHistoryAndConnect() {
        viewModelScope.launch {
            try {
                Log.d("CHAT_DEBUG", "Starting loadHistoryAndConnect for requestId: $requestId")
                
                // 1. Always fetch fresh Firebase token
                val firebaseUser = FirebaseAuth.getInstance().currentUser 
                if (firebaseUser == null) {
                    Log.e("CHAT_DEBUG", "No Firebase user found")
                    return@launch
                }
                
                val token = firebaseUser.getIdToken(true).await().token
                if (token == null) {
                    Log.e("CHAT_DEBUG", "Failed to get Firebase token")
                    return@launch
                }
                
                val authHeader = "Bearer $token"

                // Get current user ID and role from profile
                val profile = RetrofitClient.apiService.protectedCall(authHeader)

                if (profile.isSuccessful) {
                    val userData = profile.body()
                    _currentUserId.value = userData?.id
                    _currentUserRole.value = userData?.role
                    Log.d("CHAT_DEBUG", "USER ID = ${_currentUserId.value}, ROLE = ${_currentUserRole.value}")
                } else {
                    Log.e("CHAT_DEBUG", "PROFILE FAILED ${profile.code()}")
                }

                // 3. Connect WebSocket only after userId is available
                val userId = _currentUserId.value
                if (userId == null) {
                    Log.e("CHAT_DEBUG", "currentUserId is NULL after profile call")
                    return@launch
                }

                // Load Chat History with chat_type="booking"
                val response = RetrofitClient.apiService.getChatHistory(authHeader, requestId, "booking")

                if (response.isSuccessful) {
                    val history = response.body() ?: emptyList()
                    _messages.value = history
                    Log.d("CHAT_DEBUG", "MESSAGES LOADED = ${history.size}")
                } else {
                    Log.e("CHAT_DEBUG", "FAILED TO LOAD HISTORY: ${response.code()}")
                }

                // 2. Add delay before socket connect
                delay(300)

                // Determine sender role correctly
                val senderRole = when (_currentUserRole.value) {
                    "ngo" -> "ngo"
                    "caterer" -> "caterer"
                    else -> "event_organizer"
                }

                // Connect Socket
                socketManager = ChatWebSocketManager(
                    requestId = requestId,
                    token = token,
                    senderId = userId,
                    senderRole = senderRole,
                    onMessageReceived = { message ->
                        try {
                            val json = JSONObject(message)
                            val newMessage = ChatMessageResponse(
                                sender_id = json.getInt("sender_id"),
                                sender_role = json.optString("sender_role", ""),
                                message = json.getString("message"),
                                timestamp = json.getString("timestamp")
                            )
                            _messages.value = _messages.value + newMessage
                        } catch (e: Exception) {
                            Log.e("CHAT_DEBUG", "Error parsing WebSocket message: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                )
                socketManager?.connect()
                Log.d("CHAT_DEBUG", "WebSocket connected with role: $senderRole")
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Exception in loadHistoryAndConnect: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(text: String) {
        Log.d("CHAT_DEBUG", "Sending message: $text")
        socketManager?.sendMessage(text)
    }

    override fun onCleared() {
        Log.d("CHAT_DEBUG", "ViewModel cleared, disconnecting WebSocket")
        socketManager?.disconnect()
    }
}
