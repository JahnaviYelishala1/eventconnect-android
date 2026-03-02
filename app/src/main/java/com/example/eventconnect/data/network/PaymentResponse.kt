package com.example.eventconnect.data.network

data class PaymentResponse(
    val booking_id: Int,
    val amount: Double,
    val currency: String,
    val payment_method: String?,
    val card_brand: String?,
    val card_last4: String?,
    val status: String,
    val paid_at: String
)