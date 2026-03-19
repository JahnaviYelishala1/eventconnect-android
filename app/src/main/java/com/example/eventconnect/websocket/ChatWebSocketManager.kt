package com.example.eventconnect.websocket

import android.util.Log
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
    private var wsUrl: String = ""

    fun connect() {
        wsUrl = "ws://YOUR_NGROK_URL/api/chat/ws/$requestId?token=$token"
        val request = Request.Builder()
            .url(wsUrl)
            .build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("ChatWebSocket", "WebSocket connected: $wsUrl")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("ChatWebSocket", "WebSocket failure: ${t.message}", t)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.w("ChatWebSocket", "WebSocket closing: code=$code, reason=$reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.w("ChatWebSocket", "WebSocket closed: code=$code, reason=$reason")
            }
        })
    }

    fun sendMessage(text: String) {
        val json = JSONObject()
        json.put("message", text)
        json.put("sender_id", senderId)
        json.put("sender_role", senderRole)
        val sent = webSocket?.send(json.toString()) ?: false
        if (!sent) {
            Log.e("ChatWebSocket", "WebSocket send failed: $json")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, null)
    }
}