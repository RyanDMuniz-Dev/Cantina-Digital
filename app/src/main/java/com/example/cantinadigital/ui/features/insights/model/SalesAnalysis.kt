package com.example.cantinadigital.ui.features.insights.model

data class SalesAnalysis(
    val totalOrders: Int = 0,
    val totalItemsSold: Int = 0,
    val totalRevenue: Double = 0.0,
    val product: List<ProductSalesSummary> = emptyList(),
    val dailySales: List<DailySalesReport> = emptyList()
)
