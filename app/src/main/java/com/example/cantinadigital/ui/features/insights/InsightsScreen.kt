package com.example.cantinadigital.ui.features.insights

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.example.cantinadigital.data.repository.AuthRepository
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun InsightsScreen(modifier: Modifier = Modifier) {

    Column {
        Text(text = "Insight Screen")
        val repository = AuthRepository()

        val context = LocalContext.current

        Button(
            onClick = {

                repository.logout()

                (context as? Activity)?.finish()
            }
        ) {
            Text("Sair")
        }
    }

}

@Preview
@Composable
private fun InsightsScreenPreview() {
    CantinaDigitalTheme {
        InsightsScreen(

        )
    }
}
