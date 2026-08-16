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
import com.example.cantinadigital.ui.features.dashboard.model.DailySalesSummary
import com.example.cantinadigital.ui.features.dashboard.model.DashboardPeriod
import com.example.cantinadigital.ui.features.dashboard.model.DashboardUiState
import com.example.cantinadigital.ui.features.dashboard.model.ProductSalesSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

                    dailySales = buildSalesChartData(
                        orders = filteredOrders,
                        period = selectedPeriod
                    ),

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

        val calendar = Calendar.getInstance()

        calendar.set(
            Calendar.HOUR_OF_DAY,
            0
        )
        calendar.set(
            Calendar.MINUTE,
            0
        )
        calendar.set(
            Calendar.SECOND,
            0
        )
        calendar.set(
            Calendar.MILLISECOND,
            0
        )

        val startCalendar = calendar.clone() as Calendar

        when (period) {

            DashboardPeriod.TODAY -> {
                // Início de hoje.
            }

            DashboardPeriod.LAST_7_DAYS -> {
                startCalendar.add(
                    Calendar.DAY_OF_YEAR,
                    -6
                )
            }

            DashboardPeriod.LAST_30_DAYS -> {
                startCalendar.add(
                    Calendar.DAY_OF_YEAR,
                    -29
                )
            }
        }

        val endCalendar = calendar.clone() as Calendar

        endCalendar.add(
            Calendar.DAY_OF_YEAR,
            1
        )

        return startCalendar.timeInMillis to
                endCalendar.timeInMillis
    }

    private fun buildSalesChartData(
        orders: List<Order>,
        period: DashboardPeriod
    ): List<DailySalesSummary> {

        if (orders.isEmpty()) {
            return emptyList()
        }

        val locale = Locale("pt", "BR")

        return when (period) {

            DashboardPeriod.TODAY ->
                buildTodayChartData(
                    orders = orders,
                    locale = locale
                )

            DashboardPeriod.LAST_7_DAYS ->
                buildDailyChartData(
                    orders = orders,
                    days = 7,
                    locale = locale
                )

            DashboardPeriod.LAST_30_DAYS ->
                buildDailyChartData(
                    orders = orders,
                    days = 30,
                    locale = locale
                )

            DashboardPeriod.ALL ->
                buildAllTimeChartData(
                    orders = orders,
                    locale = locale
                )
        }
    }

    private fun buildTodayChartData(
        orders: List<Order>,
        locale: Locale
    ): List<DailySalesSummary> {

        val calendar = Calendar.getInstance()

        val currentHour =
            calendar.get(Calendar.HOUR_OF_DAY)

        val salesByHour =
            orders
                .filter { it.dateTime != null }
                .groupBy { order ->
                    Calendar.getInstance().apply {
                        time = order.dateTime!!.toDate()
                    }.get(Calendar.HOUR_OF_DAY)
                }
                .mapValues { (_, hourlyOrders) ->
                    hourlyOrders.sumOf {
                        it.totalValue
                    }
                }

        // Precisamos de pelo menos dois pontos
        // para o gráfico de linha fazer sentido.
        val startHour = when {
            salesByHour.isEmpty() -> currentHour
            salesByHour.keys.minOrNull()!! < currentHour ->
                salesByHour.keys.minOrNull()!!
            currentHour > 0 ->
                currentHour - 1
            else ->
                currentHour
        }

        val endHour =
            maxOf(
                currentHour,
                salesByHour.keys.maxOrNull() ?: currentHour
            )

        return (startHour..endHour).map { hour ->

            val key =
                String.format(
                    locale,
                    "%02d",
                    hour
                )

            DailySalesSummary(
                key = key,
                label = "${key}h",
                revenue = salesByHour[hour] ?: 0.0
            )
        }
    }

    private fun buildDailyChartData(
        orders: List<Order>,
        days: Int,
        locale: Locale
    ): List<DailySalesSummary> {

        val calendar =
            Calendar.getInstance()

        calendar.set(
            Calendar.HOUR_OF_DAY,
            0
        )
        calendar.set(
            Calendar.MINUTE,
            0
        )
        calendar.set(
            Calendar.SECOND,
            0
        )
        calendar.set(
            Calendar.MILLISECOND,
            0
        )

        val endCalendar =
            calendar.clone() as Calendar

        val startCalendar =
            calendar.clone() as Calendar

        startCalendar.add(
            Calendar.DAY_OF_YEAR,
            -(days - 1)
        )

        val salesByDay =
            orders
                .filter { it.dateTime != null }
                .groupBy { order ->

                    val dateCalendar =
                        Calendar.getInstance().apply {
                            time = order.dateTime!!.toDate()
                        }

                    String.format(
                        locale,
                        "%04d-%02d-%02d",
                        dateCalendar.get(Calendar.YEAR),
                        dateCalendar.get(Calendar.MONTH) + 1,
                        dateCalendar.get(Calendar.DAY_OF_MONTH)
                    )
                }
                .mapValues { (_, dailyOrders) ->
                    dailyOrders.sumOf {
                        it.totalValue
                    }
                }

        val formatter =
            SimpleDateFormat(
                "EEE",
                locale
            )

        val result =
            mutableListOf<DailySalesSummary>()

        while (!startCalendar.after(endCalendar)) {

            val key =
                String.format(
                    locale,
                    "%04d-%02d-%02d",
                    startCalendar.get(Calendar.YEAR),
                    startCalendar.get(Calendar.MONTH) + 1,
                    startCalendar.get(Calendar.DAY_OF_MONTH)
                )

            result.add(
                DailySalesSummary(
                    key = key,
                    label = formatter.format(
                        startCalendar.time
                    ).replaceFirstChar {
                        it.uppercase()
                    },
                    revenue = salesByDay[key] ?: 0.0
                )
            )

            startCalendar.add(
                Calendar.DAY_OF_YEAR,
                1
            )
        }

        return result
    }

    private fun buildAllTimeChartData(
        orders: List<Order>,
        locale: Locale
    ): List<DailySalesSummary> {

        val groupingFormat =
            SimpleDateFormat(
                "yyyy-MM-dd",
                locale
            )

        val labelFormat =
            SimpleDateFormat(
                "dd/MM",
                locale
            )

        return orders
            .filter { it.dateTime != null }
            .groupBy { order ->

                groupingFormat.format(
                    order.dateTime!!.toDate()
                )
            }
            .map { (key, groupedOrders) ->

                val date =
                    groupedOrders
                        .first()
                        .dateTime!!
                        .toDate()

                DailySalesSummary(
                    key = key,
                    label = labelFormat.format(date),
                    revenue = groupedOrders.sumOf {
                        it.totalValue
                    }
                )
            }
            .sortedBy {
                it.key
            }
    }

    private data class DashboardRawData(
        val orders: List<Order>,
        val products: List<Product>,
        val transactions: List<FinancialTransaction>,
        val logs: List<AuditLog>
    )

}