package com.example.cantinadigital.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.R
import com.example.cantinadigital.ui.features.signup.model.ThirdYearClass
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun SelectorBox(
    modifier: Modifier = Modifier,
    classList: List<String>,
    selectedClass: ThirdYearClass,
    onClassSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedCard(
            onClick = { expanded = true },
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            ),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp).height(56.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_third_year, selectedClass),
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_drop_down_24dp),
                    contentDescription = "Selecionar classe",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            classList.forEach { itemClass ->
                DropdownMenuItem(
                    text = { Text(text = itemClass) },
                    onClick = {
                        onClassSelected(itemClass)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SelectorBoxPreview() {
    CantinaDigitalTheme {
        SelectorBox(
            modifier = Modifier,
            classList = listOf("3W", "3X", "3Y"),
            selectedClass = ThirdYearClass.Y
        ) { }
    }
}