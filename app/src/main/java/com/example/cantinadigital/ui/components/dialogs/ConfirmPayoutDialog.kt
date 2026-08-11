package com.example.cantinadigital.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.ui.features.insights.model.SellerPayoutSummary

@Composable
fun ConfirmPayoutDialog(
    summary: SellerPayoutSummary,
    funcionarioNome: String,
    isProcessing: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(

        onDismissRequest = {
            if (!isProcessing) {
                onDismissRequest()
            }
        },

        title = {
            Text(
                text = "Confirmar repasse?"
            )
        },

        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Você está confirmando que o valor abaixo foi efetivamente repassado ao aluno."
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Aluno: ${summary.sellerName}",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Responsável: $funcionarioNome",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Produtos:",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                LazyColumn(
                    modifier = Modifier.height(180.dp)
                ) {

                    items(
                        summary.payoutProducts
                    ) { product ->

                        Text(
                            text = "• ${product.quantidade} x ${product.nome} — " +
                                    "R$ %.2f".format(
                                        product.valorTotal
                                    )
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = 10.dp
                    )
                )

                Text(
                    text = "Bruto: R$ %.2f".format(
                        summary.grossTotal
                    )
                )

                Text(
                    text = "Taxa: R$ %.2f".format(
                        summary.cantinaTax
                    )
                )

                Text(
                    text = "Valor repassado: R$ %.2f".format(
                        summary.liquidValueRepass
                    ),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Esta confirmação ficará registrada no sistema.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme
                        .onSurfaceVariant
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = onConfirm,
                enabled = !isProcessing
            ) {

                Text(text = if (isProcessing)
                    "Registrando..."
                else
                    "Confirmar"
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !isProcessing
            ) {

                Text(
                    text = "Cancelar"
                )
            }
        }
    )
}