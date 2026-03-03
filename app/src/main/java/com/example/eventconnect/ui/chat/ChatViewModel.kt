package com.example.eventconnect.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.ChatMessageResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.websocket.ChatWebSocketManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class ChatViewModel(
    private val bookingId: Int
) : ViewModel() {

    private val _messages =
        MutableStateFlow<List<ChatMessageResponse>>(emptyList())
    val messages: StateFlow<List<ChatMessageResponse>> = _messages

    var currentUserId: Int? = null
        private set

    private var socketManager: ChatWebSocketManager? = null

    fun loadHistory() {
        viewModelScope.launch {

            val user = FirebaseAuth.getInstance().currentUser!!
            val token = user.getIdToken(false).await().token!!

            // 🔹 Fetch current DB user id
            val profile = RetrofitClient.apiService
                .protectedCall("Bearer $token")

            if (profile.isSuccessful) {
                currentUserId = profile.body()?.id
            }

            // 🔹 Load chat history
            val response = RetrofitClient.apiService
                .getChatHistory("Bearer $token", bookingId)

            if (response.isSuccessful) {
                _messages.value = response.body() ?: emptyList()
            }
        }
    }

    fun connectSocket() {
        viewModelScope.launch {

            val user = FirebaseAuth.getInstance().currentUser!!
            val token = user.getIdToken(false).await().token!!

            socketManager = ChatWebSocketManager(
                bookingId,
                token
            ) { message ->

                val json = JSONObject(message)

                val newMessage = ChatMessageResponse(
                    sender_id = json.getInt("sender_id"),
                    message = json.getString("message"),
                    timestamp = json.getString("timestamp")
                )

                _messages.value = _messages.value + newMessage
            }

            socketManager?.connect()
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        socketManager?.sendMessage(text)
    }

    override fun onCleared() {
        socketManager?.disconnect()
    }
}