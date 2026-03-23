package com.example.eventconnect.websocket

import android.os.Handler
import android.os.Looper
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

    private val handler = Handler(Looper.getMainLooper())
    private var retryCount = 0
    private val maxRetries = 3
    private val retryDelayMs = 1000L

    fun connect() {
        // Updated WebSocket URL with chat_type=booking
        wsUrl = "wss://casemated-supercongested-loan.ngrok-free.dev/api/chat/ws/$requestId?chat_type=booking&token=$token"
        
        val request = Request.Builder()
            .url(wsUrl)
            .build()
            
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                retryCount = 0
                Log.d("ChatWebSocket", "CONNECTED")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("ChatWebSocket", "FAILED: ${t.message}", t)
                scheduleReconnect()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.w("ChatWebSocket", "WebSocket closing: code=$code, reason=$reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.w("ChatWebSocket", "CLOSED")
                scheduleReconnect()
            }
        })
    }

    private fun scheduleReconnect() {
        if (retryCount >= maxRetries) return
        retryCount++
        Log.w("ChatWebSocket", "WS RETRY $retryCount/$maxRetries")
        handler.postDelayed({ connect() }, retryDelayMs)
    }

    fun sendMessage(text: String) {
        // Prevent crash if webSocket is null
        if (webSocket == null) {
            Log.e("ChatWebSocket", "Cannot send message: WebSocket is null")
            return
        }
        
        val json = JSONObject()
        json.put("message", text)
        val sent = webSocket?.send(json.toString()) ?: false
        if (!sent) {
            Log.e("ChatWebSocket", "WebSocket send failed: $json")
        }
    }

    fun disconnect() {
        handler.removeCallbacksAndMessages(null)
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }
}