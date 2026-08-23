package com.example.cantinadigital.ui.features.insights.model

data class ProductSalesSummary(
    val productId: String = "",
    val productName: String = "",
    val quantitySold: Int = 0,
    val revenue: Double = 0.0
)