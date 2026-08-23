package com.example.cantinadigital.ui.features.insights.model

data class DailySalesReport(
    val date: String,
    val totalOrders: Int,
    val totalItems: Int,
    val revenue: Double
)