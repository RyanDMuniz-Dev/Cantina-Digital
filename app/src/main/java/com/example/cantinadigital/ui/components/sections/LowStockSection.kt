package com.example.cantinadigital.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.ui.features.dashboard.SectionTitle

@Composable
fun LowStockSection(
    products: List<Product>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        SectionTitle(
            title = "⚠️ Estoque crítico"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {

            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {

                products.forEachIndexed { index, product ->

                    LowStockRow(
                        product = product
                    )

                    if (index < products.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                horizontal = 16.dp
                            ),
                            color = MaterialTheme.colorScheme.error.copy(
                                alpha = 0.15f
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LowStockRow(
    product: Product
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = product.emoji.ifBlank { "📦" },
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.nome,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Estoque baixo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        Text(
            text = "${product.quantidade} un.",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
    }
}