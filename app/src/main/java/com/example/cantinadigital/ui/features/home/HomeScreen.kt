package com.example.cantinadigital.ui.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    Scaffold {innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Bem-Vindo!"
            )

        }

    }

}

@Preview
@Composable
private fun HomeScreenPreview() {
    CantinaDigitalTheme {
        HomeScreen()
    }
}