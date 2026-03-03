package com.example.eventconnect.data.network

data class MonthlyRevenue(
    val month: String,
    val revenue: Double
)

data class RevenueResponse(
    val total_revenue: Double,
    val total_paid_bookings: Int,
    val pending_bookings: Int,
    val this_month_revenue: Double,
    val monthly_breakdown: List<MonthlyRevenue>
)