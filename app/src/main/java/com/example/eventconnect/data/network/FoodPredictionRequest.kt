package com.example.eventconnect.data.network

data class MenuItemPrediction(
    val name: String,
    val category: String,
    val food_type: String
)

data class FoodPredictionRequest(
    val attendees: Int,
    val meal_type: String,
    val items: List<MenuItemPrediction>
)
