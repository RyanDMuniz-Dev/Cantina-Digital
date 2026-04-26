package com.example.cantinadigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cantinadigital.data.repository.AuthRepository
import com.example.cantinadigital.ui.features.signup.SignUpScreen
import com.example.cantinadigital.ui.features.signup.SignUpScreenViewModel
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            CantinaDigitalTheme {



            }

        }
    }
}
