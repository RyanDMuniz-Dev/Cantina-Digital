package com.example.cantinadigital.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.cantinadigital.data.model.OrderItem
import com.example.cantinadigital.ui.components.buttons.PrimaryLoadingButton

@Composable
fun ConfirmPaymentDialog(
    cartItems: List<OrderItem>,
    isLoading: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmPayment: (paymentType: String, receivedValue: Double, change: Double) -> Unit
) {

    var paymentType by remember { mutableStateOf("PIX") }
    var receivedValueText by remember { mutableStateOf("") }

    val totalAmount = cartItems.sumOf { it.unitValue * it.amount }
    val receivedValue = receivedValueText.replace(",", ".").trim().toDoubleOrNull() ?: 0.0
    val change = if (paymentType == "DINHEIRO" && receivedValue >= totalAmount) {
        receivedValue - totalAmount
    } else 0.0

    val isPaymentValid = paymentType == "PIX" || (paymentType == "DINHEIRO" && receivedValue >= totalAmount)
    val total = cartItems.sumOf { it.unitValue * it.amount }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                Text(
                    text = "Confirmar Pagamento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(cartItems) {item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.amount}x ${item.name}")
                            Text("R$ %.2f".format(item.unitValue * item.amount))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "R$ %.2f".format(total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Forma de pagamento:", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = paymentType == "DINHEIRO",
                            onClick = { paymentType = "DINHEIRO" }
                        )
                        Text("Dinheiro")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = paymentType == "PIX",
                            onClick = { paymentType = "PIX" }
                        )
                        Text("PIX")
                    }
                }

                if (paymentType == "DINHEIRO") {
                    OutlinedTextField(
                        value = receivedValueText,
                        onValueChange = { receivedValueText = it },
                        label = { Text("Valor Recebido (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (receivedValue >= totalAmount) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Troco:")
                            Text(
                                text = "R$ %.2f".format(change),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Voltar")
                    }

                    PrimaryLoadingButton(
                        text = "Confirmar",
                        isLoading = isLoading,
                        onClick = {
                            onConfirmPayment(
                                paymentType,
                                if (paymentType == "DINHEIRO") receivedValue else totalAmount,
                                change
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isPaymentValid
                    )
                }

            }
        }
    }

}

//@Preview (showBackground = true, showSystemUi = true)
//@Composable
//private fun ConfirmPaymentDialogPreview() {
//
//    val sampleItems = listOf(
//        OrderItem("123", "Azedinho", 1, 1.5, "Cantina", 0.0),
//        OrderItem("456", "Coca-Cola Zero", 2, 3.5, "Cantina", 0.0)
//    )
//
//    CantinaDigitalTheme {
//        ConfirmPaymentDialog(
//            cartItems = sampleItems,
//            onDismissRequest = {},
//            onConfirmPayment = { paymentType, receivedValue, change ->
//
//            },
//            isLoading = false
//        )
//    }
//}
