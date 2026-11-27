package com.example.cantinadigital.ui.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableFab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<FabMenuItem>,
    modifier: Modifier = Modifier
) {
    val scrimAlpha by animateFloatAsState(
        targetValue = if (expanded) 0.75f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "scrimAlpha"
    )

    // Rotação de 45 graus para transformar o "+" em "X"
    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "iconRotation"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Fundo escuro clicável para fechar o menu
        if (expanded || scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onExpandedChange(false) }
                    )
            )
        }
        // Coluna alinhada no canto inferior direito
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            // Lista de opções em formato de pílula
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(200)) + expandVertically(expandFrom = Alignment.Bottom),
                exit = fadeOut(tween(150)) + shrinkVertically(shrinkTowards = Alignment.Bottom)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items.forEach { item ->
                        FabPillItem(
                            item = item,
                            onClick = {
                                onExpandedChange(false)
                                item.action()
                            }
                        )
                    }
                }
            }

            // Botão principal circular (+ / X)
            FloatingActionButton(
                onClick = { onExpandedChange(!expanded) },
                shape = CircleShape,
                containerColor = if (expanded) Color(0xFF4C8DF6) else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (expanded) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (expanded) "Fechar menu" else "Novo",
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(iconRotation)
                )
            }
        }
    }
}

@Composable
private fun FabPillItem(
    item: FabMenuItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape, // Garante as bordas totalmente arredondadas (pílula)
        color = Color(0xFF2B3A55), // Tom azul escuro semelhante ao da imagem
        contentColor = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class FabMenuItem(
    val label: String,
    val icon: ImageVector,
    val action: () -> Unit
)