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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cantinadigital.ui.components.cards.ConfirmedPayoutCard
import com.example.cantinadigital.ui.components.cards.MetricCard
import com.example.cantinadigital.ui.components.cards.SellerPayoutCard
import com.example.cantinadigital.ui.components.dialogs.AddTransactionDialog
import com.example.cantinadigital.ui.components.dialogs.ConfirmPayoutDialog
import com.example.cantinadigital.ui.features.insights.model.SellerPayoutSummary

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = viewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var selectPayoutForConfimation by remember { mutableStateOf<SellerPayoutSummary?>(null) }

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
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text(
                text = "Insights e Repasses",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {

                    item {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Bruto Total",
                                value = "R$ %.2f".format(uiState.totalRevenue),
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
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
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Saldo Atual em Caixa",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(
                                        text = "R$ %.2f".format(uiState.totalBalance),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                            alpha = 0.8f
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // 1. Componente de Abas exatamente na posição indicada!
                    item {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(text = title) }
                                )
                            }
                        }
                    }

                    // 2. Renderização de acordo com a aba selecionada
                    if (selectedTabIndex == 0) {
                        // ABA: A CONFIRMAR (PENDENTES)
                        if (uiState.sellersRoyalties.isEmpty()) {
                            item {
                                Text(
                                    text = "Nenhum repasse pendente momento!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        } else {
                            items(uiState.sellersRoyalties) { summary ->
                                SellerPayoutCard(
                                    summary = summary,
                                    onConfirmPayoutSummary = {
                                        selectPayoutForConfimation = summary
                                    }
                                )
                            }
                        }
                    } else {
                        // ABA: REPASSADOS (HISTÓRICO)
                        if (uiState.confirmedPayouts.isEmpty()) {
                            item {
                                Text(
                                    text = "Nenhum repasse foi realizado ainda.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        } else {
                            items(uiState.confirmedPayouts) { payout ->
                                ConfirmedPayoutCard(payout = payout)
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
        selectPayoutForConfimation?.let { summary ->
            ConfirmPayoutDialog(
                summary = summary,
                isProcessing = uiState.isProcessingPayout,
                funcionarioNome = employeeInfo,
                onDismissRequest = { selectPayoutForConfimation = null },
                onConfirm = {
                    viewModel.confirmPayout(summary)
                    selectPayoutForConfimation = null
                }
            )
        }
    }
}