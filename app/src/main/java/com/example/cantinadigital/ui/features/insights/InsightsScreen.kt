package com.example.cantinadigital.ui.features.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.cantinadigital.ui.components.cards.ConfirmedPayoutCard
import com.example.cantinadigital.ui.components.cards.MetricCard
import com.example.cantinadigital.ui.components.cards.SalesAnalysisCard
import com.example.cantinadigital.ui.components.cards.SellerPayoutCard
import com.example.cantinadigital.ui.components.dialogs.AddTransactionDialog
import com.example.cantinadigital.ui.components.dialogs.ConfirmPayoutDialog
import com.example.cantinadigital.ui.components.selectors.PeriodSelector
import com.example.cantinadigital.ui.features.insights.model.SellerPayoutSummary

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var selectPayoutForConfirmation by remember { mutableStateOf<SellerPayoutSummary?>(null) }

    val employeeInfo by viewModel.employeeInfo.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("A confirmar", "Repassados")

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddTransactionDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Movimentar Caixa") },
                text = { Text("Lançar Caixa") }
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    )
            ) {
                Text(
                    text = "Insights e Repasses",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        bottom = 88.dp,
                    )
                ) {

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 20.dp,
                                )
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Acompanhe o desempenho financeiro e os repasses aos vendedores.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Bruto Total",
                                value = "R$ %.2f".format(uiState.totalRevenue),
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )

                            MetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Taxa Cantina",
                                value = "R$ %.2f".format(uiState.cantinaRoyalties),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Saldo atual em caixa",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "R$ %.2f".format(uiState.totalBalance),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Análise de vendas",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                PeriodSelector(
                                    selectedPeriod = uiState.selectedPeriod,
                                    onPeriodSelected = viewModel::selectPeriod
                                )
                            }

                            SalesAnalysisCard(
                                analysis = uiState.salesAnalysis,
                                isGeneratingReport = uiState.isGeneratingReport,
                                onGenerateReport = {
                                    viewModel.generateSalesReport(context)
                                }
                            )
                        }
                    }

                    // Componente de Abas exatamente na posição indicada!
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Text(
                                    text = "Repasses",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 16.dp,
                                        bottom = 8.dp
                                    )
                                )

                                SecondaryTabRow(
                                    selectedTabIndex = selectedTabIndex,
                                    modifier = Modifier.fillMaxWidth(),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ) {
                                    tabs.forEachIndexed { index, title ->
                                        Tab(
                                            selected = selectedTabIndex == index,
                                            onClick = { selectedTabIndex = index },
                                            text = {
                                                Text(
                                                    text = title,
                                                    fontWeight =
                                                        if (selectedTabIndex == index)
                                                            FontWeight.Bold
                                                        else
                                                            FontWeight.Normal
                                                )
                                            }
                                        )
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                if (selectedTabIndex == 0) {

                                    if (uiState.sellersRoyalties.isEmpty()) {
                                        Text(
                                            text = "Nenhum repasse pendente no momento.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 16.dp
                                            )
                                        )
                                    } else {
                                        uiState.sellersRoyalties.forEach { summary ->
                                            SellerPayoutCard(
                                                summary = summary,
                                                onConfirmPayoutSummary = {
                                                    selectPayoutForConfirmation = summary
                                                }
                                            )

                                            Spacer(
                                                modifier = Modifier.height(8.dp)
                                            )
                                        }
                                    }

                                } else {

                                    if (uiState.confirmedPayouts.isEmpty()) {
                                        Text(
                                            text = "Nenhum repasse foi realizado ainda.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 16.dp
                                            )
                                        )
                                    } else {
                                        uiState.confirmedPayouts.forEach { payout ->
                                            ConfirmedPayoutCard(
                                                payout = payout
                                            )

                                            Spacer(
                                                modifier = Modifier.height(8.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )
                            }
                        }
                    }

                } // end lazy column
            }
        }

        // Diálogo para lançamento de movimentação de caixa
        if (showAddTransactionDialog) {
            AddTransactionDialog(
                onDismissRequest = { showAddTransactionDialog = false },
                onConfirm = { type, valor, motivo ->
                    viewModel.addFinancialTransaction(type, valor, motivo)
                    showAddTransactionDialog = false
                }
            )
        }

        // Diálogo de confirmação de repasse
        selectPayoutForConfirmation?.let { summary ->
            ConfirmPayoutDialog(
                summary = summary,
                isProcessing = uiState.isProcessingPayout,
                funcionarioNome = employeeInfo,
                onDismissRequest = { selectPayoutForConfirmation = null },
                onConfirm = {
                    viewModel.confirmPayout(summary)
                    selectPayoutForConfirmation = null
                }
            )
        }
    }
}