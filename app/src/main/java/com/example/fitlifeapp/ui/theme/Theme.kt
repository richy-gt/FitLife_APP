package com.example.fitlifeapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 💡 Paleta de colores modo claro
private val LightColorScheme = lightColorScheme(
    primary = FitLifeGreen,
    onPrimary = FitLifeWhite,
    secondary = FitLifeBlue,
    onSecondary = FitLifeWhite,
    background = FitLifeLightGray,
    onBackground = FitLifeDark,
    surface = FitLifeWhite,
    onSurface = FitLifeDark,
    error = FitLifeError,
    onError = FitLifeWhite
)

// 💡 Paleta de colores modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = FitLifeGreen,
    onPrimary = FitLifeWhite,
    secondary = FitLifeBlue,
    onSecondary = FitLifeWhite,
    background = FitLifeDark,
    onBackground = FitLifeLightGray,
    surface = FitLifeDark,
    onSurface = FitLifeLightGray,
    error = FitLifeError,
    onError = FitLifeWhite
)

@Composable
fun FitLifeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FitLifeTypography,
        content = content
    )
}
