package com.example.cantinadigital.ui.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.model.AuditLog
import com.example.cantinadigital.data.model.FinancialTransaction
import com.example.cantinadigital.data.model.Order
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.data.repository.AuditLogRepository
import com.example.cantinadigital.data.repository.FinancialRepository
import com.example.cantinadigital.data.repository.OrderRepository
import com.example.cantinadigital.data.repository.PayoutRepository
import com.example.cantinadigital.data.repository.ProductRepository
import com.example.cantinadigital.ui.features.dashboard.model.DashboardPeriod
import com.example.cantinadigital.ui.features.dashboard.model.DashboardUiState
import com.example.cantinadigital.ui.features.dashboard.model.ProductSalesSummary
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

    companion object {
        private const val LOW_STOCK_THRESHOLD = 5
    }

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(DashboardPeriod.ALL)

    init {
        loadDashboardData()
    }

    fun selectPeriod(period: DashboardPeriod) {
        _selectedPeriod.value = period
    }

    private fun loadDashboardData() {
        viewModelScope.launch {

            val dataFlow = combine(
                orderRepository.getOrderFlow(),
                productRepository.getProductFlow(),
                financialRepository.getTransactions(),
                payoutRepository.getPayouts(),
                auditLogRepository.getLogs()
            ) { orders, products, transactions, _, logs ->

                DashboardRawData(
                    orders = orders,
                    products = products,
                    transactions = transactions,
                    logs = logs
                )
            }

            combine(
                _selectedPeriod,
                dataFlow
            ) { selectedPeriod, data ->

                val periodRange =
                    getPeriodRange(selectedPeriod)

                val filteredOrders =
                    if (periodRange == null) {
                        data.orders
                    } else {

                        val (startMillis, endMillis) =
                            periodRange

                        data.orders.filter { order ->

                            val timestamp =
                                order.dateTime
                                    ?.toDate()
                                    ?.time
                                    ?: return@filter false

                            timestamp in startMillis..<endMillis
                        }
                    }

                // ---------------------------------------------------------
                // FINANÇAS
                // ---------------------------------------------------------

                val totalRevenue =
                    filteredOrders.sumOf { it.totalValue }

                // O saldo continua GLOBAL.
                var ownProductsRevenue = 0.0
                var cantinaRoyalties = 0.0

                data.orders.forEach { order ->
                    order.items.forEach { item ->

                        val itemTotal =
                            item.unitValue * item.amount

                        if (
                            item.vendedor.equals(
                                "Cantina",
                                ignoreCase = true
                            )
                        ) {
                            ownProductsRevenue += itemTotal
                        } else {
                            cantinaRoyalties +=
                                itemTotal *
                                        (item.taxaCantina / 100.0)
                        }
                    }
                }

                val totalExits = data.transactions
                    .filter {
                        it.tipo.equals(
                            "SAIDA",
                            ignoreCase = true
                        )
                    }
                    .sumOf { it.valor }

                val totalManualEntry = data.transactions
                    .filter {
                        it.tipo.equals(
                            "ENTRADA",
                            ignoreCase = true
                        )
                    }
                    .sumOf { it.valor }

                val balance =
                    (
                            ownProductsRevenue +
                                    cantinaRoyalties +
                                    totalManualEntry
                            ) - totalExits

                val totalOrdersCount =
                    filteredOrders.size

                val averageTicket =
                    if (totalOrdersCount > 0) {
                        totalRevenue / totalOrdersCount
                    } else {
                        0.0
                    }

                // ---------------------------------------------------------
                // PRODUTOS MAIS VENDIDOS
                // ---------------------------------------------------------

                val productsById =
                    data.products.associateBy { it.id }

                val productSales = filteredOrders
                    .flatMap { it.items }
                    .groupBy { it.productId }
                    .map { (productId, items) ->

                        val product =
                            productsById[productId]

                        ProductSalesSummary(
                            productId = productId,

                            productName =
                                items
                                    .firstOrNull()
                                    ?.name
                                    .orEmpty(),

                            emoji =
                                product
                                    ?.emoji
                                    .orEmpty(),

                            quantitySold =
                                items.sumOf {
                                    it.amount
                                },

                            revenue =
                                items.sumOf {
                                    it.unitValue *
                                            it.amount
                                }
                        )
                    }
                    .sortedByDescending {
                        it.quantitySold
                    }

                val mostSoldProduct =
                    productSales.firstOrNull()

                val topProducts =
                    productSales.take(5)

                // ---------------------------------------------------------
                // ESTOQUE GLOBAL
                // ---------------------------------------------------------

                val lowStockProducts =
                    data.products
                        .filter {
                            it.quantidade <= LOW_STOCK_THRESHOLD
                        }
                        .sortedBy {
                            it.quantidade
                        }

                // ---------------------------------------------------------
                // LOGS GLOBAIS
                // ---------------------------------------------------------

                val recentLogs =
                    data.logs
                        .sortedByDescending {
                            it.dateTime
                        }
                        .take(5)

                // ---------------------------------------------------------
                // UI STATE
                // ---------------------------------------------------------

                DashboardUiState(
                    totalRevenue = totalRevenue,
                    totalBalance = balance,
                    totalOrdersCount = totalOrdersCount,
                    averageTicket = averageTicket,

                    mostSoldProduct = mostSoldProduct,
                    topProducts = topProducts,

                    dailySales = emptyList(),

                    lowStockProducts = lowStockProducts,

                    recentLogs = recentLogs,

                    selectedPeriod = selectedPeriod,

                    isLoading = false
                )

            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun getPeriodRange(
        period: DashboardPeriod
    ): Pair<Long, Long>? {

        if (period == DashboardPeriod.ALL) {
            return null
        }

        val calendar =
            java.util.Calendar.getInstance()

        calendar.set(
            java.util.Calendar.HOUR_OF_DAY,
            0
        )
        calendar.set(
            java.util.Calendar.MINUTE,
            0
        )
        calendar.set(
            java.util.Calendar.SECOND,
            0
        )
        calendar.set(
            java.util.Calendar.MILLISECOND,
            0
        )

        val startCalendar =
            calendar.clone() as java.util.Calendar

        when (period) {

            DashboardPeriod.TODAY -> {
                // Início de hoje.
            }

            DashboardPeriod.LAST_7_DAYS -> {
                startCalendar.add(
                    java.util.Calendar.DAY_OF_YEAR,
                    -6
                )
            }

            DashboardPeriod.LAST_30_DAYS -> {
                startCalendar.add(
                    java.util.Calendar.DAY_OF_YEAR,
                    -29
                )
            }
        }

        val endCalendar =
            calendar.clone() as java.util.Calendar

        endCalendar.add(
            java.util.Calendar.DAY_OF_YEAR,
            1
        )

        return startCalendar.timeInMillis to
                endCalendar.timeInMillis
    }

    private data class DashboardRawData(
        val orders: List<Order>,
        val products: List<Product>,
        val transactions: List<FinancialTransaction>,
        val logs: List<AuditLog>
    )

}