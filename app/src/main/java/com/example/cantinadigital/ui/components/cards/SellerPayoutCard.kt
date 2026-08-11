package com.example.cantinadigital.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.ui.features.insights.model.SellerPayoutSummary

@Composable
fun SellerPayoutCard(
    summary: SellerPayoutSummary,
    onConfirmPayoutSummary: (SellerPayoutSummary) -> Unit = {}
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "👤 ${summary.sellerName}",
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text = summary.statusLabel,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text =
                                if (summary.isPaid)
                                    "Pago"
                                else
                                    "Pendente",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },

                    colors = AssistChipDefaults.assistChipColors(

                        containerColor =
                            if (summary.isPaid)
                                Color(0xFFE8F5E9)
                            else
                                Color(0xFFFFF3E0),

                        labelColor =
                            if (summary.isPaid)
                                Color(0xFF2E7D32)
                            else
                                Color(0xFFE65100)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            summary.itemsSold.forEach { (itemNome, qtd) ->
                Text(
                    text = "• $qtd x $itemNome",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme
                        .onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(
                        vertical = 10.dp
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Total Vendido (Bruto):",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "R$ %.2f".format(
                            summary.grossTotal
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "(-) Taxa Cantina Retida:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    text = "R$ %.2f".format(
                            summary.cantinaTax
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Valor a Pagar ao Aluno:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "R$ %.2f".format(
                            summary.liquidValueRepass
                        ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!summary.isPaid) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {
                        onConfirmPayoutSummary(
                            summary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            "Confirmar Repasse Realizado"
                    )
                }
            }
        }
    }
}