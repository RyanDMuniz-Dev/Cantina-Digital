package com.example.cantinadigital.ui.components.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.ui.features.dashboard.model.DailySalesSummary
import com.example.cantinadigital.ui.features.dashboard.model.DashboardPeriod
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import java.util.Locale

@Composable
fun SalesChart(
    data: List<DailySalesSummary>,
    period: DashboardPeriod,
    modifier: Modifier = Modifier
) {

    val modelProducer = remember {
        CartesianChartModelProducer()
    }

    LaunchedEffect(data) {
        if (data.isEmpty()) {
            return@LaunchedEffect
        }

        modelProducer.runTransaction {
            lineModel {
                series(
                    y = data.map { it.revenue }
                )
            }
        }
    }

    val xAxisFormatter = CartesianValueFormatter { _, value, _ ->
        data.getOrNull(value.toInt())?.label ?: "?"
    }

    val yAxisFormatter = CartesianValueFormatter { _, value, _ ->
        formatChartCurrency(value)
    }

    println(
        "SALES CHART -> period=$period, dataSize=${data.size}"
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(
                            Fill(MaterialTheme.colorScheme.primary)
                        ),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            Fill(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                        Color.Transparent
                                    )
                                )
                            )
                        ),
                        interpolator = LineCartesianLayer.Interpolator.cubic(),
                    )
                )
            ),

            startAxis = VerticalAxis.rememberStart(
                valueFormatter = yAxisFormatter,
                itemPlacer = VerticalAxis.ItemPlacer.step(
                    step = { 1.0 }
                )
            ),

            bottomAxis = HorizontalAxis.rememberBottom(
                itemPlacer = HorizontalAxis.ItemPlacer.aligned(
                    spacing = { 1 },
                    shiftExtremeLines = true,
                    addExtremeLabelPadding = true
                ),
                valueFormatter = xAxisFormatter
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}

private fun formatChartCurrency(
    value: Double
) : String {
    return when {
        value >= 1000 ->
            "R$ %.1fk".format(
                Locale("pt", "BR"),
                value / 1000
            )
        else -> "R$ %.0f".format(
            Locale("pt", "BR"),
            value
        )
    }
}

private fun getXAxisItemPlacer(
    period: DashboardPeriod,
    dataSize: Int
): HorizontalAxis.ItemPlacer {

    return when (period) {

        DashboardPeriod.TODAY -> {
            HorizontalAxis.ItemPlacer.aligned(
                spacing = { 1 }
            )
        }

        DashboardPeriod.LAST_7_DAYS -> {
            HorizontalAxis.ItemPlacer.aligned(
                spacing = { 1 }
            )
        }

        DashboardPeriod.LAST_30_DAYS -> {
            HorizontalAxis.ItemPlacer.aligned(
                spacing = { 5 }
            )
        }

        DashboardPeriod.ALL -> {
            val spacing = when {
                dataSize <= 7 -> 1
                dataSize <= 30 -> 5
                dataSize <= 100 -> 10
                else -> dataSize / 8
            }

            HorizontalAxis.ItemPlacer.aligned(
                spacing = { spacing.coerceAtLeast(1) }
            )
        }
    }
}