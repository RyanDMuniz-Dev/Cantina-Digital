package com.example.cantinadigital.ui.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.data.model.Order
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun OrderItemCard(
    modifier: Modifier = Modifier,
    order: Order,
    onDeleteOrder: (Order) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Animação de rotação da setinha ao clicar
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "seta_rotacao"
    )

    // Formatação da Data e Hora do Timestamp do Firebase
    val formattedDate = remember(order.dateTime) {
        order.dateTime?.toDate()?.let { date ->
            val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR")).apply {
                timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
            }
            sdf.format(date)
        } ?: "Data indisponível"
    }

    Card(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded }, // Clica em qualquer lugar do card para expandir
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ---------------- HEADER (Sempre Visível) ----------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${order.employeeName} (${order.employeeClass})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total: R$ %.2f • %s".format(order.totalValue, order.payment),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Ícone da Seta
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Recolher detalhes" else "Expandir detalhes",
                        modifier = Modifier.rotate(rotationState)
                    )
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Cancelar Pedido",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

            }

            // ---------------- DETALHES COMPLETOS (Aparecem ao Clicar) ----------------
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

                    // Data e Hora do Pedido
                    Text(
                        text = "📅 Data/Hora: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista de Itens do Pedido
                    Text(
                        text = "Itens do Pedido:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${item.amount}x ${item.name}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "R$ %.2f".format(item.unitValue * item.amount),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Se foi pagamento em Dinheiro, mostra Troco e Recebido
                    if (order.payment.equals("DINHEIRO", ignoreCase = true)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Valor Recebido: R$ %.2f".format(order.receivedValue),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Troco: R$ %.2f".format(order.change),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Cancelar Pedido?") },
                            text = { Text("Esta ação excluirá o registro da venda e devolverá os itens ao estoque.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteDialog = false
                                        onDeleteOrder(order)
                                    }
                                ) {
                                    Text("Sim, Excluir", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }

                }
            }
        }
    }
}