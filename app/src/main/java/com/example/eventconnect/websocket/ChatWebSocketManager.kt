package com.example.eventconnect.websocket

import okhttp3.*
import org.json.JSONObject

class ChatWebSocketManager(
    private val requestId: Int,
    private val token: String,
    private val senderId: Int,
    private val senderRole: String,
    private val onMessageReceived: (String) -> Unit
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url("ws://YOUR_NGROK_URL/api/chat/ws/$requestId")
            .build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }
        })
    }

    fun sendMessage(text: String) {
        val json = JSONObject()
        json.put("message", text)
        json.put("sender_id", senderId)
        json.put("sender_role", senderRole)
        webSocket?.send(json.toString())
    }

    fun disconnect() {
        webSocket?.close(1000, null)
    }
}