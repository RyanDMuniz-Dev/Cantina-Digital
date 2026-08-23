package com.example.cantinadigital.ui.features.insights.model

data class SalesReportData(
    val periodLabel: String,
    val generatedAt: String,

    val totalOrders: Int,
    val totalItemsSold: Int,
    val totalRevenue: Double,

    val cantinaRoyalties: Double,
    val totalBalance: Double,

    val products: List<ProductSalesSummary>,
    val dailySales: List<DailySalesReport>
)