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
    primary = Color(0xFF2E4C91), //Switch
    background = Color(0xFF131212), //Fundo
    surface = Color(0xFF19C9BA),
    onPrimary = Color.White,
    /*onSecondary = Color(0xFFE36578),
    onTertiary = Color(0xFFA8E365),*/
    onBackground = Color(0xFF659FE3), //Textos
    onSurface = Color(0xFF000BD4)
)

private val LightColorScheme = lightColorScheme( //modo claro cores
    primary = Color(0xFF3F4048), //Switch
    background = Color(0xFFFFFBFE), //Fundo
    surface = Color(0xFFFFFBFE),
    /*onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,*/
    onBackground = Color(0xFF000000), //Textos
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