package com.example.cantinadigital.ui.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.repository.AuditLogRepository
import com.example.cantinadigital.data.repository.FinancialRepository
import com.example.cantinadigital.data.repository.OrderRepository
import com.example.cantinadigital.data.repository.PayoutRepository
import com.example.cantinadigital.data.repository.ProductRepository
import com.example.cantinadigital.ui.features.dashboard.model.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val financialRepository: FinancialRepository,
    private val payoutRepository: PayoutRepository,
    private val auditLogRepository: AuditLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                orderRepository.getOrderFlow(),
                productRepository.getProductFlow(),
                financialRepository.getTransactions(),
                payoutRepository.getPayouts(),
                auditLogRepository.getLogs()
            ) { orders, products, transactions, payouts, logs ->

                // Cálculo das Finanças
                val totalRevenue = orders.sumOf { it.totalValue }

                var ownProductsRevenue = 0.0
                var cantinaRoyalties = 0.0

                orders.forEach { order ->
                    order.items.forEach { item ->
                        val itemTotal = item.unitValue * item.amount
                        if (item.vendedor.equals("Cantina", ignoreCase = true)) {
                            ownProductsRevenue += itemTotal
                        } else {
                            cantinaRoyalties += itemTotal * (item.taxaCantina / 100.0)
                        }
                    }
                }

                val totalExits = transactions
                    .filter { it.tipo.equals("SAIDA", ignoreCase = true) }
                    .sumOf { it.valor }

                val totalManualEntry = transactions
                    .filter { it.tipo.equals("ENTRADA", ignoreCase = true) }
                    .sumOf { it.valor }

                val balance = (ownProductsRevenue + cantinaRoyalties + totalManualEntry) - totalExits

                // Produtos com estoque baixo (ex: <= 5 unidades)
                val lowStock = products.filter { it.quantidade <= 5 }

                DashboardUiState(
                    totalRevenue = totalRevenue,
                    totalBalance = balance,
                    totalOrdersCount = orders.size,
                    lowStockProducts = lowStock,
                    recentOrders = orders.take(5),
                    recentLogs = logs.take(5),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}