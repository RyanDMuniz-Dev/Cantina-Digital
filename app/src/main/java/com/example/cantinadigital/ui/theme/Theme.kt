package com.example.cantinadigital.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(

    primary = PrimaryBlueDark,
    secondary = Success,
    tertiary = Warning,

    background = Color(0xFF121212),
    surface = Color(0xFF1D1D1D),

    onPrimary = Color.White,

    onBackground = Color.White,
    onSurface = Color.White

)

private val LightColorScheme = lightColorScheme(

    primary = PrimaryBlueDark,
    secondary = Success,
    tertiary = Warning,

    background = Background,
    surface = Surface,

    error = Error,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,

    onBackground = TextPrimary,
    onSurface = TextPrimary

)

@Composable
fun CantinaDigitalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}