package com.example.cantinadigital.ui.features.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.model.Order
import com.example.cantinadigital.data.model.OrderItem
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.data.repository.OrderRepository
import com.example.cantinadigital.data.repository.ProductRepository
import com.example.cantinadigital.ui.features.orders.model.CreateOrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateOrderViewModel (
    private val orderRepository: OrderRepository = OrderRepository(),
    private val productRepository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems: StateFlow<List<OrderItem>> = _cartItems.asStateFlow()

    private val _uiState = MutableStateFlow<CreateOrderUiState>(CreateOrderUiState.Idle)
    val uiState: StateFlow<CreateOrderUiState> = _uiState.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            productRepository.getProductFlow().collect { productList ->
                _products.value = productList
            }
        }
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
        employeeName: String,
        employeeClass: String,
        paymentType: String,
        receivedValue: Double,
        change: Double
    ) {

        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) return

        val total = currentCart.sumOf { it.unitValue * it.amount }

        val newOrder = Order(
            employeeName = employeeName,
            employeeClass = employeeClass,
            payment = paymentType,

        )

    }

}