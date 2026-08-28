package com.example.cantinadigital.ui.features.dashboard.model

import com.example.cantinadigital.data.model.AuditLog
import com.example.cantinadigital.data.model.Product

data class DashboardUiState(
    // Resumo financeiro
    val totalRevenue: Double = 0.0,
    val totalBalance: Double = 0.0,
    val totalOrdersCount: Int = 0,
    val averageTicket: Double = 0.0,

    // Produtos vendidos
    val mostSoldProduct: ProductSalesSummary? = null,
    val topProducts: List<ProductSalesSummary> = emptyList(),

    // Dados para o gráfico
    val dailySales: List<DailySalesSummary> = emptyList(),

    // Estoque
    val lowStockProducts: List<Product> = emptyList(),

    // Atividade recente
    val recentLogs: List<AuditLog> = emptyList(),

    // Período selecionado
    val selectedPeriod: DashboardPeriod = DashboardPeriod.ALL,

    // Estado da tela
    val isLoading: Boolean = true,
    val error: String? = null
)