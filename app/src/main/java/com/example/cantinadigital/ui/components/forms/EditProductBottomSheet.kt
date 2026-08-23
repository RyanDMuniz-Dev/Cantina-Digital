package com.example.cantinadigital.ui.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cantinadigital.R
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.ui.components.fields.SimpleFormTextField
import com.example.cantinadigital.ui.components.radio.ProductOriginOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductBottomSheet(
    modifier: Modifier = Modifier,
    productToEdit: Product,
    onDismissRequest: () -> Unit,
    onConfirmUpdate: (Product) -> Unit,
    onConfirmDelete: (Product) -> Unit
) {

    var emoji by remember { mutableStateOf(productToEdit.emoji) }
    var name by remember { mutableStateOf(productToEdit.nome) }
    var value by remember { mutableStateOf(productToEdit.valor.toString()) }
    var amount by remember { mutableStateOf(productToEdit.quantidade.toString()) }

    var isProductCantina by remember { mutableStateOf(productToEdit.vendedor.equals("Cantina", ignoreCase = true)) }
    var seller by remember { mutableStateOf(productToEdit.vendedor) }
    var cantinaTax by remember { mutableStateOf(productToEdit.cantinaTaxa.toString()) }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val isValueValid = value.replace(",", ".").trim().toDoubleOrNull() != null
    val isAmountValid = amount.trim().toIntOrNull() != null
    val isFormValid = name.isNotBlank() && isValueValid && isAmountValid

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji.ifBlank { "📦" },
                            fontSize = 24.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Editar produto",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = name.ifBlank { "Produto sem nome" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Linha 1: Emoji + Nome
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SimpleFormTextField(
                    modifier = Modifier.weight(0.3f),
                    label = R.string.emoji,
                    value = emoji,
                    singleLine = true,
                    onValueChanged = { if (it.length <= 2) emoji = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                SimpleFormTextField(
                    modifier = Modifier.weight(0.7f),
                    label = R.string.label_name,
                    value = name,
                    onValueChanged = { name = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    singleLine = true
                )
            }

            // Linha 2: Preço + Quantidade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SimpleFormTextField(
                    value = value,
                    onValueChanged = { value = it },
                    label = R.string.price,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                SimpleFormTextField(
                    value = amount,
                    onValueChanged = { amount = it },
                    label = R.string.amount,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Origem do produto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Defina quem é responsável pelo produto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProductOriginOption(
                        modifier = Modifier.weight(1f),
                        title = "Cantina",
                        description = "Produto próprio",
                        selected = isProductCantina,
                        onClick = {
                            isProductCantina = true
                        }
                    )

                    ProductOriginOption(
                        modifier = Modifier.weight(1f),
                        title = "Aluno",
                        description = "Produto de aluno",
                        selected = !isProductCantina,
                        onClick = {
                            isProductCantina = false
                        }
                    )
                }
            }

            if (!isProductCantina) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Informações do vendedor",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = seller,
                            onValueChange = { seller = it },
                            label = { Text("Nome do aluno") },
                            modifier = Modifier.weight(0.6f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = cantinaTax,
                            onValueChange = { cantinaTax = it },
                            label = { Text("Taxa (%)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier.weight(0.4f),
                            singleLine = true
                        )

                    }

                }
            }

            // Botão Salvar Alterações
            Button(
                onClick = {
                    val sanitizedValue =
                        value.replace(",", ".").trim().toDoubleOrNull() ?: 0.0

                    val sanitizedAmount =
                        amount.trim().toIntOrNull() ?: 0

                    val sanitizedTax =
                        if (isProductCantina) {
                            0.0
                        } else {
                            cantinaTax
                                .replace(",", ".")
                                .trim()
                                .toDoubleOrNull() ?: 0.0
                        }

                    val sanitizedSeller =
                        if (isProductCantina) {
                            "Cantina"
                        } else {
                            seller.trim()
                        }

                    val updatedProduct = productToEdit.copy(
                        emoji = emoji.ifBlank { "📦" },
                        nome = name.trim(),
                        valor = sanitizedValue,
                        quantidade = sanitizedAmount,
                        vendedor = sanitizedSeller,
                        cantinaTaxa = sanitizedTax
                    )

                    onConfirmUpdate(updatedProduct)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = isFormValid,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text("Salvar alterações")
            }

            // Botão Excluir Produto (Estilo de Alerta/Erro)
            TextButton(
                onClick = {
                    showDeleteConfirmation = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Excluir produto",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(text = "Excluir Produto") },
            text = { Text(text = "Tem certeza que deseja excluir '${productToEdit.nome}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onConfirmDelete(productToEdit)
                    }
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

}