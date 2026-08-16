package com.example.cantinadigital.ui.components.selectors

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.example.cantinadigital.ui.features.dashboard.model.DashboardPeriod

@Composable
fun PeriodSelector(
    selectedPeriod: DashboardPeriod,
    onPeriodSelected: (DashboardPeriod) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        TextButton(
            onClick = {
                expanded = true
            }
        ) {
            Text(
                text = "${selectedPeriod.label} ▼",
                fontWeight = FontWeight.SemiBold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            DashboardPeriod.entries.forEach { period ->

                DropdownMenuItem(
                    text = {
                        Text(period.label)
                    },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}