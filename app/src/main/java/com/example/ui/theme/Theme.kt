package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val ShajeenColorScheme = lightColorScheme(
    primary = BrandPrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3EEFA),
    onPrimaryContainer = BrandHeaderColor,
    secondary = BrandPrimaryDarkBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5F5FD),
    onSecondaryContainer = BrandHeaderColor,
    tertiary = BrandGold,
    onTertiary = Color(0xFF332000),
    background = BrandBackgroundStart,
    onBackground = BrandTextPrimary,
    surface = BrandCardSurface,
    onSurface = BrandTextPrimary,
    surfaceVariant = Color(0xFFF0F9FD),
    onSurfaceVariant = BrandTextSecondary,
    outline = BrandCardBorder,
    error = StatusRejected,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Provide Arabic RTL layout direction across the entire app
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = ShajeenColorScheme,
            typography = Typography,
            content = content
        )
    }
}
