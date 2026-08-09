package com.example.cantinadigital.ui.components.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@SuppressLint("DefaultLocale")
@Composable
fun StockItemCard(
    modifier: Modifier = Modifier,
    item: Product,
    onEditClick: (Product) -> Unit
) {
    
    val amountBgColor = if (item.quantidade == 0) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val amountTextColor = if (item.quantidade == 0) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Lado Esquerdo: Emoji + Informações Principais
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ícone/Emoji com fundo circular
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.emoji,
                        fontSize = 24.sp
                    )
                }

                // Nome e Vendedor
                Column {
                    Text(
                        text = item.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Vendedor: ${item.vendedor}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // Lado Direito: Valor + Badge de Quantidade
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = String.format("R$ %.2f", item.valor),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Chip / Badge mostrando a quantidade disponível
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = amountBgColor
                ) {
                    Text(
                        text = "${item.quantidade} em estoque",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = amountTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = { onEditClick(item) }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "edit: ${item.nome}",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

        }
    }
}

@Preview
@Composable
private fun StockItemPreview() {
    CantinaDigitalTheme {
        StockItemCard(
            item = Product(
                nome = "Pirulito",
                emoji = "🍭",
                vendedor = "Cantina",
                quantidade = 20,
                valor = 2.30
            ),
            onEditClick = {}
        )
    }
}