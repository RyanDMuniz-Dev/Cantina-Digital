package com.example.cantinadigital.ui.features.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.model.FinancialTransaction
import com.example.cantinadigital.data.model.Order
import com.example.cantinadigital.data.repository.FinancialRepository
import com.example.cantinadigital.data.repository.OrderRepository
import com.example.cantinadigital.ui.features.insights.model.InsightsUiState
import com.example.cantinadigital.ui.features.insights.model.SellerPayoutSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class InsightsViewModel (
    private val orderRepository: OrderRepository = OrderRepository(),
    private val financialRepository: FinancialRepository = FinancialRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                orderRepository.getOrderFlow(),
                financialRepository.getTransactions()
            ) { orders, transactions ->
                processInsights(orders, transactions)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun processInsights(
        orders: List<Order>,
        transaction: List<FinancialTransaction>
    ) : InsightsUiState {

        var totalRevenue = 0.0
        var cantinaRoyalties = 0.0

        val productSeller = mutableMapOf<String, MutableList<Pair<String, Int>>>()
        val grossSeller = mutableMapOf<String, Double>()
        val sellerTax = mutableMapOf<String, Double>()

        for (order in orders) {

            totalRevenue += order.totalValue

            for (item in order.items) {

                val itemTotal = item.unitValue * item.amount

                if (!item.vendedor.equals("Cantina", ignoreCase = true)) {
                    val taxPercent = item.taxaCantina / 100.0
                    val taxValue = itemTotal * taxPercent

                    cantinaRoyalties += taxValue

                    grossSeller[item.vendedor] = (grossSeller[item.vendedor] ?: 0.0) + itemTotal
                    sellerTax[item.vendedor] = (sellerTax[item.vendedor] ?: 0.0) + taxValue

                    val list = productSeller.getOrPut(item.vendedor) { mutableListOf() }
                    list.add(Pair(item.name,item.amount))
                }

            }

        } // for orders

        val totalExits = transaction.filter { it.tipo.equals("SAIDA", ignoreCase = true) }.sumOf { it.valor }
        val totalManualEntry = transaction.filter { it.tipo.equals("ENTRADA", ignoreCase = true) }.sumOf { it.valor }

        val balance = (totalRevenue + totalManualEntry) - totalExits

        val repassesList = grossSeller.map { (seller, gross) ->
            val tax = sellerTax[seller] ?: 0.0
            val groupedProducts = productSeller[seller]
                ?.groupBy { it.first }
                ?.map { (name, list) -> Pair(name, list.sumOf { it.second }) }
                ?: emptyList()

            SellerPayoutSummary(
                sellerName = seller,
                itemsSold = groupedProducts,
                grossTotal = gross,
                cantinaTax = tax,
                liquidValueRepass = gross - tax
            )
        }

        return InsightsUiState(
            totalRevenue = totalRevenue,
            cantinaRoyalties = cantinaRoyalties,
            totalExits = totalExits,
            totalBalance = balance,
            sellersRoyalties = repassesList,
            isLoading = false
        )

    }

    fun addFinancialTransaction(type: String, value: Double, reason: String) {
        viewModelScope.launch {
            val transaction = FinancialTransaction(
                tipo = type,
                valor = value,
                motivo = reason,
                funcionarioNome = "Atendente"
            )
            financialRepository.addTransaction(transaction)
        }
    }

}