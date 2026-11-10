package com.Hefputu5.Nugsis.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TigerColorScheme = darkColorScheme(
    primary = Palette.Jade,
    onPrimary = Palette.OnAccent,
    primaryContainer = Palette.SurfaceRaised,
    onPrimaryContainer = Palette.TextPrimary,
    secondary = Palette.Amber,
    onSecondary = Palette.OnAccent,
    secondaryContainer = Palette.SurfaceAlt,
    onSecondaryContainer = Palette.TextPrimary,
    tertiary = Palette.Gold,
    onTertiary = Palette.OnAccent,
    background = Palette.BackgroundBase,
    onBackground = Palette.TextPrimary,
    surface = Palette.Surface,
    onSurface = Palette.TextPrimary,
    surfaceVariant = Palette.SurfaceRaised,
    onSurfaceVariant = Palette.TextSecondary,
    outline = Palette.TextMuted,
    error = Palette.Crimson,
    onError = Palette.TextPrimary
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TigerColorScheme,
        typography = AppTypography,
        content = content
    )
}
