package com.example.eventconnect.websocket

import okhttp3.*
import org.json.JSONObject

class NgoWebSocketManager(
    private val firebaseToken: String,
    private val onSurplusAlert: (SurplusAlert) -> Unit
) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {

        val request = Request.Builder()
            .url("wss://casemated-supercongested-loan.ngrok-free.dev/ws/ngo?token=$firebaseToken")
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    println("NGO WebSocket Connected")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {

                    try {

                        val json = JSONObject(text)

                        val type = json.optString("type")

                        if (type == "surplus_food") {

                            val alert = SurplusAlert(
                                requestId = json.getInt("request_id"),
                                eventName = json.getString("event_name"),
                                foodDescription = json.getString("food_description"),
                                imageUrl = if (json.isNull("image_url")) null else json.getString("image_url"),
                                distance = json.getDouble("distance"),
                                latitude = json.getDouble("latitude"),
                                longitude = json.getDouble("longitude")
                            )

                            onSurplusAlert(alert)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    println("WebSocket closing: $reason")
                    webSocket.close(1000, null)
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    println("WebSocket error: ${t.message}")
                }
            }
        )
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
    }
}