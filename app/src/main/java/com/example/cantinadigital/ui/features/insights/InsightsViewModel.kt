package com.example.cantinadigital.ui.features.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.model.FinancialTransaction
import com.example.cantinadigital.data.model.Order
import com.example.cantinadigital.data.model.Payout
import com.example.cantinadigital.data.model.PayoutProduct
import com.example.cantinadigital.data.repository.FinancialRepository
import com.example.cantinadigital.data.repository.OrderRepository
import com.example.cantinadigital.data.repository.PayoutAlreadyConfirmedException
import com.example.cantinadigital.data.repository.PayoutRepository
import com.example.cantinadigital.ui.features.insights.model.InsightsUiState
import com.example.cantinadigital.ui.features.insights.model.SellerPayoutSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class InsightsViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val financialRepository: FinancialRepository = FinancialRepository(),
    private val payoutRepository: PayoutRepository = PayoutRepository()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(InsightsUiState())

    val uiState: StateFlow<InsightsUiState> =
        _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {

        viewModelScope.launch {

            combine(
                orderRepository.getOrderFlow(),
                financialRepository.getTransactions(),
                payoutRepository.getPayouts()
            ) { orders, transactions, payouts ->

                processInsights(
                    orders = orders,
                    transactions = transactions,
                    payouts = payouts
                )

            }.collect { state ->

                _uiState.value = state
            }
        }
    }

    private fun processInsights(
        orders: List<Order>,
        transactions: List<FinancialTransaction>,
        payouts: List<Payout>
    ): InsightsUiState {

        var totalRevenue = 0.0
        var cantinaRoyalties = 0.0

        val productSeller =
            mutableMapOf<String, MutableList<PayoutProduct>>()

        val grossSeller =
            mutableMapOf<String, Double>()

        val sellerTax =
            mutableMapOf<String, Double>()

        /*
         * Cada item já repassado vira uma chave:
         *
         * pedidoId + vendedor
         */
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

                val itemTotal =
                    item.unitValue * item.amount

                /*
                 * A Cantina é tratada separadamente.
                 */
                if (
                    item.vendedor.equals(
                        "Cantina",
                        ignoreCase = true
                    )
                ) {
                    continue
                }

                val seller =
                    item.vendedor.trim()

                val taxPercent =
                    item.taxaCantina / 100.0

                val taxValue =
                    itemTotal * taxPercent

                /*
                 * Receita histórica da Cantina.
                 *
                 * Continua considerando todas as vendas.
                 */
                cantinaRoyalties += taxValue

                /*
                 * Verificamos especificamente
                 * pedido + vendedor.
                 */
                val payoutKey =
                    createPayoutKey(
                        orderId = order.id,
                        sellerName = seller
                    )

                /*
                 * Já foi repassado?
                 */
                if (payoutKey in paidKeys) {
                    continue
                }

                grossSeller[seller] =
                    (grossSeller[seller] ?: 0.0) + itemTotal

                sellerTax[seller] =
                    (sellerTax[seller] ?: 0.0) + taxValue

                val payoutProduct =
                    PayoutProduct(
                        pedidoId = order.id,
                        produtoId = item.productId,
                        nome = item.name,
                        quantidade = item.amount,
                        valorUnitario = item.unitValue,
                        valorTotal = itemTotal,
                        taxaCantina = item.taxaCantina
                    )

                productSeller
                    .getOrPut(seller) {
                        mutableListOf()
                    }
                    .add(payoutProduct)
            }
        }

        /*
         * Movimentações manuais.
         */
        val totalExits =
            transactions
                .filter {
                    it.tipo.equals(
                        "SAIDA",
                        ignoreCase = true
                    )
                }
                .sumOf { it.valor }

        val totalManualEntry =
            transactions
                .filter {
                    it.tipo.equals(
                        "ENTRADA",
                        ignoreCase = true
                    )
                }
                .sumOf { it.valor }

        /*
         * Esse saldo continua sendo o saldo geral da cantina.
         */
        val balance =
            (totalRevenue + totalManualEntry) - totalExits

        /*
         * Montamos os cards dos vendedores
         * que ainda possuem dinheiro pendente.
         */
        val repassesList =
            grossSeller.map { (seller, gross) ->

                val tax =
                    sellerTax[seller] ?: 0.0

                val products =
                    productSeller[seller]
                        ?: emptyList()

                val groupedProducts =
                    products
                        .groupBy { it.nome }
                        .map { (name, list) ->

                            Pair(
                                name,
                                list.sumOf {
                                    it.quantidade
                                }
                            )
                        }

                SellerPayoutSummary(
                    sellerName = seller,
                    statusLabel = "Repasse pendente",
                    isPaid = false,
                    itemsSold = groupedProducts,
                    grossTotal = gross,
                    cantinaTax = tax,
                    liquidValueRepass = gross - tax,
                    payoutProducts = products
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

    suspend fun confirmPayout(
        summary: SellerPayoutSummary,
        funcionarioId: String,
        funcionarioNome: String
    ): Result<Unit> {

        if (funcionarioNome.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "Nome do funcionário não identificado."
                )
            )
        }

        if (summary.payoutProducts.isEmpty()) {
            return Result.failure(
                IllegalArgumentException(
                    "Não existem produtos pendentes para este repasse."
                )
            )
        }

        val payout =
            Payout(

                vendedor =
                    summary.sellerName,

                valorBruto =
                    summary.grossTotal,

                taxaCantina =
                    summary.cantinaTax,

                valorRepassado =
                    summary.liquidValueRepass,

                produtos =
                    summary.payoutProducts,

                funcionarioId =
                    funcionarioId,

                funcionarioNome =
                    funcionarioNome
            )

        return payoutRepository.createPayout(
            payout
        )
    }

    fun addFinancialTransaction(
        type: String,
        value: Double,
        reason: String
    ) {

        viewModelScope.launch {

            val transaction =
                FinancialTransaction(
                    tipo = type,
                    valor = value,
                    motivo = reason,
                    funcionarioNome = "Atendente"
                )

            financialRepository
                .addTransaction(transaction)
        }
    }

    private fun createPayoutKey(
        orderId: String,
        sellerName: String
    ): String {

        return "${orderId}|${sellerName.trim().lowercase()}"
    }
}