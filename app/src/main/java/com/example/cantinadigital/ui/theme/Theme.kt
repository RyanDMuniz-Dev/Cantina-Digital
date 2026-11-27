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
    primary = PrimaryBlueDark, //Switch
    secondary = Success,
    tertiary = Warning,

    background = Color(0xFF121212),
    surface = Color(0xFF1D1D1D),

    onPrimary = Color.White,

    onBackground = Color.White,
    onSurface = Color.White //provavelmente iria precisar dessas quatro cores abaixo

    /*background = Color(0xFF131212), //Fundo
    surface = Color(0xFF19C9BA),
    onPrimary = Color.White,
    onBackground = Color(0xFF659FE3), //Textos
    onSurface = Color(0xFF000BD4),*/
)

private val LightColorScheme = lightColorScheme( //modo claro cores
    primary = PrimaryBlueDark, //Switch
    secondary = Success,
    tertiary = Warning,
    background = Background,
    surface = Surface, //se for usar o modo escuro eu vou precisar das quatro cores abaixo
    /*background = Color(0xFFFFFBFE), //Fundo
    surface = Color(0xFFFFFBFE)
    onBackground = TextPrimary,
    onSurface = TextPrimary,*/
    onBackground = Color(0xFF000000), //Textos
    onSurface = Color(0xFFD3C1F3),

    error = Error,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
)

@Composable
fun CantinaDigitalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), //eu precisaria provavelmente colocar 'false' aqui
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