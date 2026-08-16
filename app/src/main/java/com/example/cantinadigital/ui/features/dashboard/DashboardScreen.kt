package com.example.cantinadigital.ui.features.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.cantinadigital.ui.components.sections.DashboardFinancialSection
import com.example.cantinadigital.ui.components.sections.LowStockSection
import com.example.cantinadigital.ui.components.sections.MostSoldProductSection
import com.example.cantinadigital.ui.components.sections.TopProductsSection
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->

        if (uiState.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (uiState.error != null) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "Erro ao carregar dashboard",
                    color = MaterialTheme.colorScheme.error
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Resumo da cantina",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Visão geral das vendas, produtos e estoque.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }

                item {
                    DashboardFinancialSection(
                        balance = uiState.totalBalance,
                        revenue = uiState.totalRevenue,
                        ordersCount = uiState.totalOrdersCount,
                        averageTicket = uiState.averageTicket,
                        selectedPeriod = uiState.selectedPeriod,
                        onPeriodSelected = viewModel::selectPeriod
                    )
                }

                item {
                    SalesChartPlaceholder()
                }

                item {
                    MostSoldProductSection(
                        product = uiState.mostSoldProduct
                    )
                }

                item {
                    TopProductsSection(
                        products = uiState.topProducts
                    )
                }

                if (uiState.lowStockProducts.isNotEmpty()) {
                    item {
                        LowStockSection(
                            products = uiState.lowStockProducts
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// TODO: terminar de fazer o gráfico
@Composable
private fun SalesChartPlaceholder() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        SectionTitle(
            title = "Vendas"
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "📊",
                    style = MaterialTheme.typography.displaySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gráfico de vendas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "A evolução das vendas será exibida aqui.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

fun formatCurrency(value: Double): String {
    return NumberFormat
        .getCurrencyInstance(Locale("pt", "BR"))
        .format(value)
}

private fun formatTime(date: Date?): String {
    if (date == null) {
        return "--:--"
    }

    return SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(date)
}