package com.example.cantinadigital.ui.features.dashboard.model

data class ProductSalesSummary(
    val productId: String = "",
    val productName: String = "",
    val emoji: String = "",
    val quantitySold: Int = 0,
    val revenue: Double
)
