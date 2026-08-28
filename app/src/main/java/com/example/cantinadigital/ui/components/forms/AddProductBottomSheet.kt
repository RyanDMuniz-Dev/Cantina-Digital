package com.example.cantinadigital.ui.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.R
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.ui.components.buttons.PrimaryLoadingButton
import com.example.cantinadigital.ui.components.fields.SimpleFormTextField
import com.example.cantinadigital.ui.components.selectors.SelectorBox
import com.example.cantinadigital.ui.features.signup.model.ThirdYearClass
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBottomSheet(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (Product) -> Unit
) {

    var emoji by remember { mutableStateOf("📦") }
    var nome by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    // 1. Declare os estados no topo do Composable:
    // Estado do Enum para o SelectorBox exibir a seleção
    var selectedClassEnum by remember { mutableStateOf(ThirdYearClass.W) }
    var sala by remember { mutableStateOf(selectedClassEnum.name) }

    var vendedor by remember { mutableStateOf("") }
    var isProductCantina by remember { mutableStateOf(true) }
    var cantinaTax by remember { mutableStateOf("20") }

    val isFormValid = nome.isNotBlank() && valor.isNotBlank() && quantidade.isNotBlank()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Add new product",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SimpleFormTextField(
                    modifier = Modifier.weight(0.3F),
                    label = R.string.emoji,
                    value = emoji,
                    singleLine = true,
                    onValueChanged = { if (it.length <= 2) emoji = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    )
                )

                SimpleFormTextField(
                    modifier = Modifier.weight(0.8f),
                    label = R.string.label_name,
                    value = nome,
                    onValueChanged = { nome = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // campo do Preço
                SimpleFormTextField(
                    value = valor,
                    onValueChanged = { valor = it },
                    label = R.string.price,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                // Campo da Quantidade
                SimpleFormTextField(
                    value = quantidade,
                    onValueChanged = { quantidade = it },
                    label = R.string.amount,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

            }

            // origin selector
            Text(
                text = "Vendedor do produto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isProductCantina,
                        onClick = { isProductCantina = true }
                    )
                    Text(
                        text = "Cantina"
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !isProductCantina,
                        onClick = { isProductCantina = false }
                    )
                    Text(
                        text = "Aluno"
                    )
                }
            }

            if (!isProductCantina) {
                Column(
                    verticalArrangement = Arrangement.Center
                )
                {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top // 1. Alinha todos os elementos pelo topo
                    ) {
                        SimpleFormTextField(
                            modifier = Modifier.weight(0.7f),
                            value = vendedor,
                            label = R.string.vendedor,
                            singleLine = true,
                            onValueChanged = { vendedor = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            )
                        )

                        SelectorBox(
                            modifier = Modifier
                                .weight(0.3f)
                                .padding(top = 13.dp), // 2. Desce o caixa do Selector em 8dp para emparelhar com a borda do TextField
                            classList = listOf("W", "X", "Y"),
                            selectedClass = selectedClassEnum,
                            onClassSelected = { selectedString ->
                                sala = selectedString
                                selectedClassEnum = runCatching {
                                    ThirdYearClass.valueOf(selectedString)
                                }.getOrDefault(ThirdYearClass.W)
                            }
                        )
                    }

                    SimpleFormTextField(
                        modifier = Modifier.fillMaxWidth().padding(),
                        value = cantinaTax,
                        label = R.string.tax,
                        singleLine = true,
                        onValueChanged = { cantinaTax = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )

                }
            }

            // Botão de Salvar
            PrimaryLoadingButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Cadastrar Produto",
                isLoading = isLoading,
                enabled = isFormValid,
                onClick = {
                    val sanitizedValue = valor.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val sanitizedAmount = quantidade.trim().toIntOrNull() ?: 0
                    val sanitizedTax = if (isProductCantina) 0.0 else (cantinaTax.replace(",", ".").trim().toDoubleOrNull() ?: 0.0)
                    val sanitizedSeller = if (isProductCantina) "Cantina" else vendedor.trim()
                    val sanitizedClass = if (isProductCantina) "WXY" else sala.trim().uppercase()

                    val newProduct = Product(
                        emoji = emoji.ifBlank { "📦" },
                        nome = nome.trim(),
                        valor = sanitizedValue,
                        quantidade = sanitizedAmount,
                        vendedor = sanitizedSeller,
                        sala = sanitizedClass,
                        cantinaTaxa = sanitizedTax
                    )
                    onConfirmRequest(newProduct)
                },
            )
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddProductBottomSheetPreview() {
    CantinaDigitalTheme {
        AddProductBottomSheet(
            onDismissRequest = {},
            onConfirmRequest = {},
            isLoading = false
        )
    }
}