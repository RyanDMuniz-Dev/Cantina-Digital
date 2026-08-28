package com.example.cantinadigital.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.ui.components.cards.SimpleMetricCard
import com.example.cantinadigital.ui.components.selectors.PeriodSelector
import com.example.cantinadigital.ui.features.dashboard.SectionTitle
import com.example.cantinadigital.ui.features.dashboard.formatCurrency
import com.example.cantinadigital.ui.features.dashboard.model.DashboardPeriod

@Composable
fun DashboardFinancialSection(
    balance: Double,
    revenue: Double,
    ordersCount: Int,
    averageTicket: Double,
    selectedPeriod: DashboardPeriod,
    onPeriodSelected: (DashboardPeriod) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(
                title = "Resumo financeiro"
            )

            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = onPeriodSelected
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SimpleMetricCard(
                emoji = "💰",
                title = "Saldo em Caixa",
                value = formatCurrency(balance),
                modifier = Modifier.weight(1f)
            )

            SimpleMetricCard(
                emoji = "📈",
                title = "Vendas",
                value = formatCurrency(revenue),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SimpleMetricCard(
                emoji = "🧾",
                title = "Pedidos",
                value = ordersCount.toString(),
                modifier = Modifier.weight(1f)
            )

            SimpleMetricCard(
                emoji = "🎫",
                title = "Valor médio por venda",
                value = formatCurrency(averageTicket),
                modifier = Modifier.weight(1f)
            )
        }
    }
}