package com.example.a10101010.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MonochromeColorScheme = darkColorScheme(
    primary = LauncherWhite,
    onPrimary = LauncherBlack,
    secondary = LauncherWhite,
    onSecondary = LauncherBlack,
    tertiary = LauncherWhite,
    onTertiary = LauncherBlack,
    background = LauncherBlack,
    onBackground = LauncherWhite,
    surface = LauncherBlack,
    onSurface = LauncherWhite,
    surfaceVariant = LauncherBlack,
    onSurfaceVariant = LauncherWhite,
    outline = LauncherWhite,
    error = LauncherWhite,
    onError = LauncherBlack
)

@Composable
fun MonochromeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MonochromeColorScheme,
        typography = Typography,
        content = content
    )
}