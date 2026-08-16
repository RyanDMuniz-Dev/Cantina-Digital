package com.example.cantinadigital.ui.features.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.cantinadigital.ui.components.cards.AuditLogCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    onBackClick: () -> Unit = {},
    viewModel: LogsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val categories = listOf("Tudo", "Produtos", "Caixa", "Repasses", "Pedidos")

    val filteredLogs = when (selectedTabIndex) {
        1 -> uiState.logs.filter { it.type.equals("PRODUTO", ignoreCase = true) }
        2 -> uiState.logs.filter { it.type.equals("CAIXA", ignoreCase = true) }
        3 -> uiState.logs.filter { it.type.equals("REPASSE", ignoreCase = true) }
        4 -> uiState.logs.filter { it.type.equals("PEDIDO", ignoreCase = true) }
        else -> uiState.logs
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Atividades", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchLogs() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar Logs"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1 // Garante que o texto fique sempre em uma única linha
                            )
                        }
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.fetchLogs() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredLogs.isEmpty() && !uiState.isLoading) {
                    Text(
                        text = uiState.error ?: "Nenhuma atividade registrada nesta categoria.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filteredLogs,
                            key = { it.id }
                        ) { log ->
                            AuditLogCard(
                                log = log,
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 6.dp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}