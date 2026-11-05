package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TaimoorOrangeLight,
    onPrimary = Color.White,
    primaryContainer = TaimoorOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = TaimoorGold,
    onSecondary = Color.Black,
    tertiary = TaimoorGreen,
    background = DarkBg,
    onBackground = Color(0xFFE4E9F0),
    surface = DarkSurface,
    onSurface = Color(0xFFE4E9F0),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB5C0CE),
    outline = Color(0xFF32455C)
)

private val LightColorScheme = lightColorScheme(
    primary = TaimoorOrange,
    onPrimary = Color.White,
    primaryContainer = TaimoorOrangeContainer,
    onPrimaryContainer = TaimoorOrangeDark,
    secondary = TaimoorNavy,
    onSecondary = Color.White,
    secondaryContainer = NeutralSurfaceVariant,
    onSecondaryContainer = TaimoorNavy,
    tertiary = TaimoorGreen,
    background = NeutralLightBg,
    onBackground = TextPrimary,
    surface = NeutralSurface,
    onSurface = TextPrimary,
    surfaceVariant = NeutralSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
