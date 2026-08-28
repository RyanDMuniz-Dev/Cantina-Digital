package com.example.cantinadigital.ui.components.rows

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.ui.features.dashboard.formatCurrency
import com.example.cantinadigital.ui.features.dashboard.model.ProductSalesSummary

@Composable
fun TopProductRow(
    position: Int,
    product: ProductSalesSummary
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
            text = "$position",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(28.dp)
        )

        Text(
            text = product.emoji.ifBlank { "📦" },
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.productName,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = formatCurrency(product.revenue),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${product.quantitySold} un.",
            fontWeight = FontWeight.Bold
        )
    }
}