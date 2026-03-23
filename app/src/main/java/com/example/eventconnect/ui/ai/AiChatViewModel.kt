package com.example.eventconnect.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.AiChatRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AiChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    fun sendMessage(message: String, bookingId: Int) {
        if (message.isBlank()) return

        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser!!
                val token = user.getIdToken(true).await().token!!

                // Optimistic UI append
                val userMsg = ChatMessage(text = message, isUser = true)
                _messages.value = _messages.value + userMsg

                _isTyping.value = true

                val response = RetrofitClient.apiService.sendAiMessage(
                    "Bearer $token",
                    AiChatRequest(message = message, booking_id = bookingId)
                )

                if (response.isSuccessful) {
                    val reply = response.body()?.reply.orEmpty()
                    _messages.value = _messages.value + ChatMessage(text = reply, isUser = false)
                } else {
                    _messages.value = _messages.value + ChatMessage(text = "Sorry, I encountered an error (${response.code()}).", isUser = false)
                }

            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(text = "I'm having trouble connecting right now.", isUser = false)
                e.printStackTrace()
            } finally {
                _isTyping.value = false
            }
        }
    }
}
