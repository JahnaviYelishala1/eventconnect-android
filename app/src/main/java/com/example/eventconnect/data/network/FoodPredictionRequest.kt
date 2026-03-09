package com.example.eventconnect.data.network

class MenuItemPrediction(
    val name: String,
    val category: String
)

class FoodPredictionRequest(
    val attendees: Int,
    val event_type: String,
    val meal_type: String,
    val items: List<MenuItemPrediction>
)
