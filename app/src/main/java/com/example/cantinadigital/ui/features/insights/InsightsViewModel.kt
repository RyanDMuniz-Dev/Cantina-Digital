package com.example.cantinadigital.ui.features.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.model.FinancialTransaction
import com.example.cantinadigital.data.model.Order
import com.example.cantinadigital.data.model.Payout
import com.example.cantinadigital.data.model.PayoutProduct
import com.example.cantinadigital.data.repository.AuditLogRepository
import com.example.cantinadigital.data.repository.AuthRepository
import com.example.cantinadigital.data.repository.FinancialRepository
import com.example.cantinadigital.data.repository.OrderRepository
import com.example.cantinadigital.data.repository.PayoutRepository
import com.example.cantinadigital.ui.features.dashboard.model.DashboardPeriod
import com.example.cantinadigital.ui.features.insights.model.InsightsUiState
import com.example.cantinadigital.ui.features.insights.model.ProductSalesSummary
import com.example.cantinadigital.ui.features.insights.model.SalesAnalysis
import com.example.cantinadigital.ui.features.insights.model.SellerPayoutSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val financialRepository: FinancialRepository,
    private val payoutRepository: PayoutRepository,
    private val authRepository: AuthRepository,
    private val auditLogRepository: AuditLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())

    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    private val _employeeInfo = MutableStateFlow("")

    val employeeInfo: StateFlow<String> = _employeeInfo.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(DashboardPeriod.ALL)
    val selectedPeriod: StateFlow<DashboardPeriod> = _selectedPeriod.asStateFlow()

    init {
        loadData()
        getEmployeeInfo()
    }

    private fun loadData() {

        viewModelScope.launch {

            combine(
                orderRepository.getOrderFlow(),
                financialRepository.getTransactions(),
                payoutRepository.getPayouts(),
                _selectedPeriod
            ) { orders, transactions, payouts, period ->

                processInsights(
                    orders = orders,
                    transactions = transactions,
                    payouts = payouts,
                    selectedPeriod = period
                )

            }.collect { state ->

                _uiState.value = state
            }
        }
    }

    private fun processInsights(
        orders: List<Order>,
        transactions: List<FinancialTransaction>,
        payouts: List<Payout>,
        selectedPeriod: DashboardPeriod
    ): InsightsUiState {

        var totalRevenue = 0.0
        var cantinaRoyalties = 0.0
        var ownProductsRevenue = 0.0 // Receita de produtos próprios da cantina

        val productSeller = mutableMapOf<String, MutableList<PayoutProduct>>()
        val grossSeller = mutableMapOf<String, Double>()
        val sellerTax = mutableMapOf<String, Double>()
        val sellerClassMap = mutableMapOf<String, String>()

        val paidKeys = payouts
            .flatMap { payout ->
                payout.produtos.map { product ->
                    createPayoutKey(
                        orderId = product.pedidoId,
                        sellerName = payout.vendedor
                    )
                }
            }
            .toSet()

        for (order in orders) {

            totalRevenue += order.totalValue

            for (item in order.items) {

                val itemTotal = item.unitValue * item.amount

                /*
                 * Produtos da própria Cantina: 100% da receita entra para o caixa da cantina.
                 */
                if (item.vendedor.equals("Cantina", ignoreCase = true)) {
                    ownProductsRevenue += itemTotal
                    continue
                }

                val seller = item.vendedor.trim()

                if (item.sala.isNotBlank()) {
                    sellerClassMap[seller] = item.sala
                }

                val taxPercent = item.taxaCantina / 100.0
                val taxValue = itemTotal * taxPercent

                /*
                 * Receita total de taxas (royalties) da cantina.
                 */
                cantinaRoyalties += taxValue

                val payoutKey = createPayoutKey(
                    orderId = order.id,
                    sellerName = seller
                )

                /*
                 * Se já foi repassado, pula a inclusão nos repasses pendentes.
                 */
                if (payoutKey in paidKeys) {
                    continue
                }

                grossSeller[seller] = (grossSeller[seller] ?: 0.0) + itemTotal
                sellerTax[seller] = (sellerTax[seller] ?: 0.0) + taxValue

                val payoutProduct = PayoutProduct(
                    pedidoId = order.id,
                    produtoId = item.productId,
                    nome = item.name,
                    quantidade = item.amount,
                    valorUnitario = item.unitValue,
                    valorTotal = itemTotal,
                    taxaCantina = item.taxaCantina
                )

                productSeller
                    .getOrPut(seller) { mutableListOf() }
                    .add(payoutProduct)
            }
        }

        /*
         * Movimentações manuais do caixa.
         */
        val totalExits = transactions
            .filter { it.tipo.equals("SAIDA", ignoreCase = true) }
            .sumOf { it.valor }

        val totalManualEntry = transactions
            .filter { it.tipo.equals("ENTRADA", ignoreCase = true) }
            .sumOf { it.valor }

        /*
         * Saldo REAL do Caixa da Cantina:
         * Receita das vendas da própria Cantina + Taxas cobradas dos alunos + Suprimentos manuais
         * DEDUZINDO: Apenas Sangrias manuais (Retiradas).
         */

        val balance = (ownProductsRevenue + cantinaRoyalties + totalManualEntry) - totalExits

        val repassesList = grossSeller.map { (seller, gross) ->
            val tax = sellerTax[seller] ?: 0.0
            val products = productSeller[seller] ?: emptyList()

            val groupedProducts = products
                .groupBy { it.nome }
                .map { (name, list) ->
                    Pair(name, list.sumOf { it.quantidade })
                }

            SellerPayoutSummary(
                sellerName = seller,
                sellerClass = sellerClassMap[seller] ?: "",
                statusLabel = "Repasse pendente",
                isPaid = false,
                itemsSold = groupedProducts,
                grossTotal = gross,
                cantinaTax = tax,
                liquidValueRepass = gross - tax,
                payoutProducts = products
            )
        }

        val salesAnalysis = buildSalesAnalysis(
            orders = orders,
            period = selectedPeriod
        )

        return InsightsUiState(
            totalRevenue = totalRevenue,
            cantinaRoyalties = cantinaRoyalties,
            totalExits = totalExits,
            totalBalance = balance,
            sellersRoyalties = repassesList,
            confirmedPayouts = payouts,

            selectedPeriod = selectedPeriod,
            salesAnalysis = salesAnalysis,

            isLoading = false
        )
    }

    private fun createPayoutKey(
        orderId: String,
        sellerName: String
    ): String {

        return "${orderId}|${sellerName.trim().lowercase()}"
    }

    private fun buildSalesAnalysis(
        orders: List<Order>,
        period: DashboardPeriod
    ) : SalesAnalysis {

        val filteredOrders = filterOrdersByPeriod(
            orders = orders,
            period = period
        )

        val productMap = mutableMapOf<String, ProductSalesSummary>()

        var totalItemsSold = 0
        var totalRevenue = 0.0

        filteredOrders.forEach { order ->

            totalRevenue += order.totalValue

            order.items.forEach { item ->

                val itemRevenue = item.unitValue * item.amount

                totalItemsSold += item.amount

                val key = item.productId.ifBlank {
                    item.name.trim().lowercase()
                }

                val existing = productMap[key]

                if (existing == null) {

                    productMap[key] = ProductSalesSummary(
                        productId = item.productId,
                        productName = item.name,
                        quantitySold = item.amount,
                        revenue = itemRevenue
                    )

                } else {

                    productMap[key] = existing.copy(
                        quantitySold = existing.quantitySold + item.amount,
                        revenue = existing.revenue + itemRevenue
                    )

                }

            }

        }

        return SalesAnalysis(
            totalOrders = filteredOrders.size,
            totalItemsSold = totalItemsSold,
            totalRevenue = totalRevenue,
            product = productMap.values.sortedByDescending { it.quantitySold }
        )

    }

    private fun filterOrdersByPeriod(
        orders: List<Order>,
        period: DashboardPeriod
    ): List<Order> {

        val now = System.currentTimeMillis()

        val startTime = when (period) {

            DashboardPeriod.ALL -> {
                return orders
            }

            DashboardPeriod.TODAY -> {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            DashboardPeriod.LAST_7_DAYS -> {
                now - (7L * 24L * 60L * 60L * 1000L)
            }

            DashboardPeriod.LAST_30_DAYS -> {
                now - (30L * 24L * 60L * 60L * 1000L)
            }
        }

        return orders.filter { order ->
            val timestamp = order.dateTime ?: return@filter false

            timestamp.toDate().time >= startTime
        }
    }

    fun confirmPayout(summary: SellerPayoutSummary) {
        viewModelScope.launch {
            val userResult = authRepository.getDadosUsuarioLogado()
            val userData = userResult.getOrNull()

            val employeeName = userData?.get("nome") as? String ?: "Atendente"
            val employeeId = userData?.get("uid") as? String ?: ""

            val payout = Payout(
                vendedor = summary.sellerName,
                valorBruto = summary.grossTotal,
                taxaCantina = summary.cantinaTax,
                valorRepassado = summary.liquidValueRepass,
                produtos = summary.payoutProducts,
                funcionarioId = employeeId,
                funcionarioNome = employeeName,
                dataHora = com.google.firebase.Timestamp.now() // <-- Garante que a data/hora seja gravada!
            )

            payoutRepository.createPayout(payout)

            auditLogRepository.logAction(
                type = "REPASSE",
                action = "CONFIRMAR",
                description = "Confirmou repasse de R$ %.2f para %s".format(summary.liquidValueRepass, summary.sellerName),
                username = employeeName
            )
        }
    }

    fun getEmployeeInfo() {

        viewModelScope.launch {

            val userResult = authRepository.getDadosUsuarioLogado()

            val userData = userResult.getOrNull()

            val employeeName = userData?.get("nome") as? String
            val employeeClass = userData?.get("turma") as? String

            _employeeInfo.value = "$employeeName $employeeClass".trim()

        }

    }

    fun addFinancialTransaction(
        type: String,
        value: Double,
        reason: String
    ) {
        viewModelScope.launch {
            // 1. Busca os dados do usuário conectado (Nome e Turma/Sala)
            val userResult = authRepository.getDadosUsuarioLogado()
            val userData = userResult.getOrNull()

            val userName = userData?.get("nome") as? String ?: "Atendente"
            val userClass = userData?.get("turma") as? String ?: ""

            // 2. Monta e envia a transação financeira
            val transaction = FinancialTransaction(
                tipo = type,
                valor = value,
                motivo = reason,
                funcionarioNome = userName
            )

            financialRepository.addTransaction(transaction)

            // 3. Registra o Log de Auditoria para o Caixa
            val actionName = if (type == "ENTRADA") "SUPRIMENTO" else "RETIRADA"
            val actionLabel = if (type == "ENTRADA") "Entrada" else "Saída"

            auditLogRepository.logAction(
                type = "CAIXA",
                action = actionName,
                description = "Lançamento de $actionLabel manual: R$ %.2f - Motivo: %s".format(value, reason),
                username = userName,
                userClass = userClass
            )
        }
    }

    fun selectPeriod(period: DashboardPeriod) {
        _selectedPeriod.value = period
    }

}