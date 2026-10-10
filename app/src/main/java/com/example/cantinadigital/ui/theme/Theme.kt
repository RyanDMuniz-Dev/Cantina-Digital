package com.example.cantinadigital.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme( //modo escuro cores
    primary = Color(0xFF3F4048),
    background = Color(0xFFB7DA85),
    surface = Color(0xFF278A56),
    /*onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,*/
    onBackground = Color(0xFF3F303F),
    onSurface = Color(0xFF878694)
)

private val LightColorScheme = lightColorScheme( //modo claro cores
    primary = Color(0xFF3F4048),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    /*onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,*/
    onBackground = Color(0xFF471EB2),
    onSurface = Color(0xFFD3C1F3)
)

@Composable
fun CantinaDigitalTheme(
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if(darkTheme){
        DarkColorScheme
    } else{
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}