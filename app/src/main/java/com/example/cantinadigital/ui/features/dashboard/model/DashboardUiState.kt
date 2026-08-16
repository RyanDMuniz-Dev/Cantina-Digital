package com.example.cantinadigital.ui.features.dashboard.model

import com.example.cantinadigital.data.model.AuditLog
import com.example.cantinadigital.data.model.Order
import com.example.cantinadigital.data.model.Product

data class DashboardUiState(
    val totalRevenue: Double = 0.0,
    val totalBalance: Double = 0.0,
    val totalOrdersCount: Int = 0,
    val lowStockProducts: List<Product> = emptyList(),
    val recentOrders: List<Order> = emptyList(),
    val recentLogs: List<AuditLog> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)