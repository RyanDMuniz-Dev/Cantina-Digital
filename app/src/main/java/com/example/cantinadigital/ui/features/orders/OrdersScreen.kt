package com.example.cantinadigital.ui.features.orders

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cantinadigital.ui.components.cards.OrderItemCard
import com.example.cantinadigital.ui.components.dialogs.ConfirmPaymentDialog
import com.example.cantinadigital.ui.components.dialogs.CreateOrderDialog
import com.example.cantinadigital.ui.features.orders.model.CreateOrderUiState
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@Composable
fun OrdersScreen(
    viewModel: CreateOrderViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val recentOrders by viewModel.recentOrders.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showCreateOrderDialog by remember { mutableStateOf(false) }
    var showConfirmPaymentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateOrderUiState.Success -> {
                Toast.makeText(context,"Pedido registrado com sucesso!", Toast.LENGTH_SHORT).show()
                showConfirmPaymentDialog = false
                viewModel.resetUiState()
            }
            is CreateOrderUiState.Error -> {
                val errorMsg = (uiState as CreateOrderUiState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    val groupedOrders = remember(recentOrders) {
        recentOrders.groupBy { getGroupHeaderForDate(it.dateTime?.toDate()) }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateOrderDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Novo Pedido") },
                text = { Text("Novo Pedido") }
            )
        }
    ) { paddingValues ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(12.dp),
                text = "Histórico de Vendas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (recentOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum pedido registrado ainda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = 88.dp
                    )
                ) {

                    groupedOrders.forEach { (headerTitle, ordersInGroup) ->

                        item {
                            Text(
                                text = headerTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 4.dp).fillMaxWidth()
                            )
                        }

                        items(ordersInGroup) { order ->
                            OrderItemCard(
                                order = order,
                                onDeleteOrder = { orderToDelete ->
                                    viewModel.deleteOrder(orderToDelete)
                                }
                            )
                        }
                    }

                }
            }

            if (showCreateOrderDialog) {
                CreateOrderDialog(
                    products = products,
                    cartItems = cartItems,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onAddProduct = { viewModel.addProductToCart(it) },
                    onRemoveProduct = { viewModel.removeProductFromCart(it) },
                    onDismissRequest = {
                        showCreateOrderDialog = false
                        viewModel.clearCart()
                    },
                    onProceedToPayment = {
                        showCreateOrderDialog = false
                        showConfirmPaymentDialog = true
                    }
                )
            }

            if (showConfirmPaymentDialog) {
                ConfirmPaymentDialog(
                    cartItems = cartItems,
                    isLoading = uiState is CreateOrderUiState.Loading,
                    onDismissRequest = {
                        showConfirmPaymentDialog = false
                        showCreateOrderDialog = true
                    },
                    onConfirmPayment = { paymentType, receivedValue, change ->
                        viewModel.confirmAndRegisterOrder(
                            paymentType = paymentType,
                            receivedValue = receivedValue,
                            change = change
                        )
                    }
                )
            }

        }

    }

}

private fun getGroupHeaderForDate(date: Date?): String {
    if (date == null) return "Outros"

    val timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
    val now = Calendar.getInstance(timeZone)
    val target = Calendar.getInstance(timeZone).apply { time = date }

    val currentWeek = now.get(Calendar.WEEK_OF_YEAR)
    val currentYear = now.get(Calendar.YEAR)

    val targetWeek = target.get(Calendar.WEEK_OF_YEAR)
    val targetYear = target.get(Calendar.YEAR)

    return when (currentYear) {
        targetYear if currentWeek == targetWeek -> "Esta semana"
        targetYear if currentWeek - targetWeek == 1 -> "Semana passada"
        else -> "Semanas anteriores"
    }

}