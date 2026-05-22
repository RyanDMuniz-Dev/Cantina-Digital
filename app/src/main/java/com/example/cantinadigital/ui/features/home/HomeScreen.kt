package com.example.cantinadigital.ui.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val name = "Ryan"

    Scaffold {innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Welcome, $name",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,

            )

            Spacer(modifier = Modifier.padding(20.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Lucro total:"
                )
            }

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