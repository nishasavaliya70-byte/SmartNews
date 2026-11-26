package com.smartnewsai.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = PrimaryNavy,
    primaryContainer = PrimaryNavy,
    onPrimaryContainer = AccentCyan,
    secondary = SecondaryBlue,
    onSecondary = LightCardBackground,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkCardBackground,
    onSurface = DarkText,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = DarkSecondaryText,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    onPrimary = LightCardBackground,
    primaryContainer = LightBackground,
    onPrimaryContainer = PrimaryNavy,
    secondary = SecondaryBlue,
    onSecondary = LightCardBackground,
    tertiary = AccentCyan,
    background = LightBackground,
    onBackground = PrimaryText,
    surface = LightCardBackground,
    onSurface = PrimaryText,
    surfaceVariant = LightBorder,
    onSurfaceVariant = SecondaryText,
    error = ErrorRed
)

@Composable
fun SmartNewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
