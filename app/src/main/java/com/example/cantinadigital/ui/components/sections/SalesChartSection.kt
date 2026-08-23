package com.example.cantinadigital.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.ui.components.charts.SalesChart
import com.example.cantinadigital.ui.features.dashboard.SectionTitle
import com.example.cantinadigital.ui.features.dashboard.model.DailySalesSummary
import com.example.cantinadigital.ui.features.dashboard.model.DashboardPeriod

@Composable
fun SalesChartSection(
    data: List<DailySalesSummary>,
    period: DashboardPeriod
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        SectionTitle(
            title = "📈 Vendas"
        )

        Text(
            text = getSalesPeriodDescription(period),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            if (data.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma venda no período.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            } else {

                SalesChart(
                    data = data,
                    period = period,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

private fun getSalesPeriodDescription(
    period: DashboardPeriod
): String {
    return when (period) {
        DashboardPeriod.ALL ->
            "Histórico completo de vendas"

        DashboardPeriod.TODAY ->
            "Desempenho das vendas de hoje"

        DashboardPeriod.LAST_7_DAYS ->
            "Desempenho das vendas nos últimos 7 dias"

        DashboardPeriod.LAST_30_DAYS ->
            "Desempenho das vendas nos últimos 30 dias"
    }
}