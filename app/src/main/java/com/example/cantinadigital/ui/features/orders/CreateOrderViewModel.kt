package com.example.cantinadigital.ui.features.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.model.Order
import com.example.cantinadigital.data.model.OrderItem
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.data.repository.AuditLogRepository
import com.example.cantinadigital.data.repository.AuthRepository
import com.example.cantinadigital.data.repository.OrderRepository
import com.example.cantinadigital.data.repository.ProductRepository
import com.example.cantinadigital.ui.features.orders.model.CreateOrderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository,
    private val auditLogRepository: AuditLogRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems: StateFlow<List<OrderItem>> = _cartItems.asStateFlow()

    private val _uiState = MutableStateFlow<CreateOrderUiState>(CreateOrderUiState.Idle)
    val uiState: StateFlow<CreateOrderUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _recentOrders = MutableStateFlow<List<Order>>(emptyList())
    val recentOrders: StateFlow<List<Order>> = _recentOrders.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(_products, _searchQuery) { products, query ->
        if (query.isBlank()) {
            products
        } else {
            products.filter {
                it.nome.contains(query, ignoreCase = true) ||
                        it.emoji.contains(query) ||
                        it.vendedor.contains(query)
            }
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchProducts()
        fetchOrders()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            productRepository.getProductFlow().collect { productList ->
                _products.value = productList
            }
        }
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            orderRepository.getOrderFlow().collect { orders ->
                _recentOrders.value = orders
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addProductToCart(product: Product) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.productId == product.id }
            if (existingItem != null) {
                currentItems.map { item ->
                    if (item.productId == product.id) {
                        item.copy(amount = item.amount + 1)
                    } else item
                }
            } else {
                currentItems + OrderItem(
                    productId = product.id,
                    name = product.nome,
                    amount = 1,
                    unitValue = product.valor,
                    vendedor = product.vendedor,
                    sala = product.sala,
                    taxaCantina = product.cantinaTaxa
                )
            }
        }
    }

    fun removeProductFromCart(productId: String) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.productId == productId }
            if (existingItem != null && existingItem.amount > 1) {
                currentItems.map { item ->
                    if (item.productId == productId)
                        item.copy(amount = item.amount - 1)
                    else item
                }
            } else {
                currentItems.filterNot { it.productId == productId }
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun confirmAndRegisterOrder(
        paymentType: String,
        receivedValue: Double,
        change: Double
    ) {
        if (_uiState.value == CreateOrderUiState.Loading) return

        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) return

        val total = currentCart.sumOf { it.unitValue * it.amount }

        viewModelScope.launch {
            _uiState.value = CreateOrderUiState.Loading

            val userResult = authRepository.getDadosUsuarioLogado()
            val userData = userResult.getOrNull()

            val employeeName = userData?.get("nome") as? String ?: "Atendente"
            val employeeClass = userData?.get("turma") as? String ?: ""

            val newOrder = Order(
                employeeName = employeeName,
                employeeClass = employeeClass,
                payment = paymentType,
                receivedValue = receivedValue,
                change = change,
                items = currentCart,
                totalValue = total
            )

            val success = orderRepository.createOrder(newOrder)
            if (success) {
                _cartItems.value = emptyList()
                _uiState.value = CreateOrderUiState.Success

                // Grava o log de auditoria
                auditLogRepository.logAction(
                    type = "PEDIDO",
                    action = "CRIAR",
                    description = "Registrou novo pedido no valor de R$ %.2f (%s)".format(total, paymentType),
                    username = employeeName,
                    userClass = employeeClass
                )
            } else {
                _uiState.value = CreateOrderUiState.Error("Falha ao registrar pedido")
            }
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            val userResult = authRepository.getDadosUsuarioLogado()
            val userData = userResult.getOrNull()

            val employeeName = userData?.get("nome") as? String ?: "Atendente"
            val employeeClass = userData?.get("turma") as? String ?: ""

            val success = orderRepository.deleteOrder(order)
            if (success) {
                // Grava o log de auditoria ao cancelar/deletar pedido
                auditLogRepository.logAction(
                    type = "PEDIDO",
                    action = "CANCELAR",
                    description = "Cancelou o pedido de R$ %.2f".format(order.totalValue),
                    username = employeeName,
                    userClass = employeeClass
                )
            } else {
                _uiState.value = CreateOrderUiState.Error("Erro ao excluir o pedido")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = CreateOrderUiState.Idle
    }
}