package com.example.eventconnect.websocket

import android.util.Log
import okhttp3.*
import org.json.JSONObject

class NgoWebSocketManager(
    private val firebaseToken: String,
    private val onSurplusAlert: (SurplusAlert) -> Unit
) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        // Updated URL to use the one from RetrofitClient (wss version)
        val request = Request.Builder()
            .url("wss://casemated-supercongested-loan.ngrok-free.dev/ws/ngo?token=$firebaseToken")
            .build()

        Log.d("NGO_WS", "Attempting to connect to: ${request.url}")

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("NGO_WS", "CONNECTED")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("NGO_WS", "RECEIVING MESSAGE: $text")
                    try {
                        val json = JSONObject(text)
                        val type = json.optString("type")

                        if (type == "surplus_food") {
                            val alert = SurplusAlert(
                                requestId = json.getInt("request_id"),
                                sender_id = json.optInt("sender_id", 0),
                                eventName = json.getString("event_name"),
                                foodDescription = json.getString("food_description"),
                                imageUrl = if (json.isNull("image_url")) null else json.optString("image_url", null),
                                distance = json.getDouble("distance"),
                                latitude = json.getDouble("latitude"),
                                longitude = json.getDouble("longitude")
                            )
                            Log.d("NGO_WS", "PARSED ALERT: $alert")
                            onSurplusAlert(alert)
                        }
                    } catch (e: Exception) {
                        Log.e("NGO_WS", "PARSE ERROR: ${e.message}")
                        e.printStackTrace()
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("NGO_WS", "CLOSING: $reason (code: $code)")
                    webSocket.close(1000, null)
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    Log.e("NGO_WS", "FAILED: ${t.message}")
                    t.printStackTrace()
                }
            }
        )
    }

    fun disconnect() {
        Log.d("NGO_WS", "DISCONNECTING")
        webSocket?.close(1000, "User disconnected")
    }
}
