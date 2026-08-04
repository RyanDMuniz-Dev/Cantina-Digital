package com.example.cantinadigital.ui.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.R
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.ui.components.fields.SimpleFormTextField
import org.w3c.dom.Text

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
    var seller by remember { mutableStateOf(productToEdit.vendedor) }

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
            Text(
                text = "Editar Produto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

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

            // Campo Vendedor
            OutlinedTextField(
                value = seller,
                onValueChange = { seller = it },
                label = { Text("Vendedor") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Botão Salvar Alterações
            Button(
                onClick = {
                    val sanitizedValue = value.replace(",", ".").trim().toDoubleOrNull() ?: 0.0
                    val sanitizedAmount = amount.trim().toIntOrNull() ?: 0

                    val updatedProduct = productToEdit.copy(
                        emoji = emoji.ifBlank { "📦" },
                        nome = name,
                        valor = sanitizedValue,
                        quantidade = sanitizedAmount,
                        vendedor = seller
                    )
                    onConfirmUpdate(updatedProduct)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Salvar Alterações")
            }

            // Botão Excluir Produto (Estilo de Alerta/Erro)
            OutlinedButton(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Excluir Produto")
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