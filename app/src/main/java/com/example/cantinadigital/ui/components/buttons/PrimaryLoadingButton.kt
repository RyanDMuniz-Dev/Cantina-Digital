package com.example.cantinadigital.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryLoadingButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {

    Button(
        // Desabilita o botão se estiver carregando ou se o parâmetro 'enabled' for falso
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
    ) {
        if (isLoading) {
            // "O negócio girando": indicador de progresso circular dentro do botão
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = text)
        }
    }

}