package com.example.eventconnect.websocket

import okhttp3.*

class ChatWebSocketManager(
    private val bookingId: Int,
    private val token: String,
    private val onMessageReceived: (String) -> Unit
) {

    private var webSocket: WebSocket? = null

    fun connect() {

        val request = Request.Builder()
            .url("wss://casemated-supercongested-loan.ngrok-free.dev/ws/chat/$bookingId?token=$token")
            .build()

        val client = OkHttpClient()

        webSocket = client.newWebSocket(request,
            object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, text: String) {
                    onMessageReceived(text)
                }
            })
    }

    fun sendMessage(message: String) {
        val json = """{"message":"$message"}"""
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, null)
    }
}