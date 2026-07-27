package com.example.cantinadigital.ui.components.appbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Preview
@Composable
private fun BottomTopBarPreview() {
    CantinaDigitalTheme (
        darkTheme = true
    ) {
        Scaffold(
            topBar = {
                CantinaTopBar(
                    onProfileClick = {},
                    onNotificationClick = {}
                )
            },
            bottomBar = {
                CantinaBottomBar(
                    selected = NavigationItem.Dashboard
                ) { }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            }
        }
    }
}