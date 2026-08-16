package com.example.cantinadigital.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.ui.components.rows.TopProductRow
import com.example.cantinadigital.ui.features.dashboard.SectionTitle
import com.example.cantinadigital.ui.features.dashboard.model.ProductSalesSummary

@Composable
fun TopProductsSection(
    products: List<ProductSalesSummary>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        SectionTitle(
            title = "🔥 Mais vendidos"
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            if (products.isEmpty()) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Nenhuma venda registrada.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            } else {

                Column(
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {

                    products.forEachIndexed { index, product ->

                        TopProductRow(
                            position = index + 1,
                            product = product
                        )

                        if (index < products.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}