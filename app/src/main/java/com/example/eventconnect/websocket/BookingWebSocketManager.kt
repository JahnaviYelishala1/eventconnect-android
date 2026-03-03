package com.example.eventconnect.websocket

import okhttp3.*

class BookingWebSocketManager(
    private val userId: String,
    private val onMessage: (String) -> Unit
) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {

        val request = Request.Builder()
            .url("wss://casemated-supercongested-loan.ngrok-free.dev/ws/$userId")
            .build()

        webSocket = client.newWebSocket(request,
            object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    println("Booking WebSocket Connected")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    onMessage(text)
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    println("Booking WebSocket Error: ${t.message}")
                }
            })
    }

    fun disconnect() {
        webSocket?.close(1000, null)
    }
}