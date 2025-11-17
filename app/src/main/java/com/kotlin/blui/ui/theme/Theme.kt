package com.kotlin.blui.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark Color Scheme dengan Blue Theme
private val DarkColorScheme = darkColorScheme(
    primary = BlueNormal,
    onPrimary = Color.White,
    primaryContainer = BlueDark,
    onPrimaryContainer = BlueLight,

    secondary = BlueDarkHover,
    onSecondary = Color.White,
    secondaryContainer = BlueDarkActive,
    onSecondaryContainer = BlueLightActive,

    tertiary = BlueLightHover,
    onTertiary = BlueDarker,

    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE3E2E6),

    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),

    surfaceVariant = BlueDarker,
    onSurfaceVariant = BlueLight,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

// Light Color Scheme dengan Blue Theme
private val LightColorScheme = lightColorScheme(
    primary = BlueNormal,
    onPrimary = Color.White,
    primaryContainer = BlueLight,
    onPrimaryContainer = BlueDarker,

    secondary = BlueNormalHover,
    onSecondary = Color.White,
    secondaryContainer = BlueLightActive,
    onSecondaryContainer = BlueDarkActive,

    tertiary = BlueDark,
    onTertiary = Color.White,

    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),

    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E),

    surfaceVariant = BlueLight,
    onSurfaceVariant = BlueDark,

    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun BluiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to always use custom blue theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}