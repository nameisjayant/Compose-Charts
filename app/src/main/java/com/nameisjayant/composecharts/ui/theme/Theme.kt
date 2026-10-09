package com.nameisjayant.composecharts.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = OceanDark,
    onPrimary = Night,
    primaryContainer = OceanTintDark,
    onPrimaryContainer = OceanDark,
    secondary = SkyDark,
    onSecondary = Night,
    background = Night,
    onBackground = InkDark,
    surface = NightSurface,
    onSurface = InkDark,
    surfaceVariant = OceanTintDark,
    onSurfaceVariant = SlateDark,
    surfaceContainer = NightSurface,
    outlineVariant = HairlineDark,
)

private val LightColorScheme = lightColorScheme(
    primary = Ocean,
    onPrimary = Color.White,
    primaryContainer = OceanTint,
    onPrimaryContainer = Ocean,
    secondary = Sky,
    onSecondary = Color.White,
    background = Mist,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = OceanTint,
    onSurfaceVariant = Slate,
    surfaceContainer = Color.White,
    outlineVariant = Hairline,
)

@Composable
fun ComposeChartsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Off by default so the showcase keeps its brand palette; wallpaper colours clash with the charts.
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}