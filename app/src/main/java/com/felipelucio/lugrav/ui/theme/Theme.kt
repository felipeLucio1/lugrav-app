package com.felipelucio.lugrav.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = OnPrimary,
    primaryContainer = SkyBlue,
    onPrimaryContainer = SkyBlueDark,
    secondary = SkyBlueDark,
    onSecondary = OnPrimary,
    background = SurfaceColor,
    onBackground = Color(0xFF1C1B1F),
    surface = SurfaceColor,
    onSurface = Color(0xFF1C1B1F),
    error = ErrorColor,
    onError = OnPrimary
)

@Composable
fun LugravTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SkyBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}