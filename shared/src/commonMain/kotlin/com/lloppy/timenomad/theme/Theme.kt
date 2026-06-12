package com.lloppy.timenomad.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val Gold = Color(0xFFE8C36B)
private val GoldDim = Color(0xFFB9923D)
private val Teal = Color(0xFF3FC9C0)
private val Indigo = Color(0xFF6E63D6)

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF1A1407),
    primaryContainer = Color(0xFF3A2F12),
    onPrimaryContainer = Color(0xFFF6E4B8),
    secondary = Teal,
    onSecondary = Color(0xFF00201E),
    secondaryContainer = Color(0xFF0E3B38),
    onSecondaryContainer = Color(0xFFBDF3EE),
    tertiary = Indigo,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2A2570),
    onTertiaryContainer = Color(0xFFE0DCFF),
    background = Color(0xFF0B0B14),
    onBackground = Color(0xFFE9E7F0),
    surface = Color(0xFF14141F),
    onSurface = Color(0xFFE9E7F0),
    surfaceVariant = Color(0xFF20202E),
    onSurfaceVariant = Color(0xFF9D9CB0),
    outline = Color(0xFF3A3A4C),
    outlineVariant = Color(0xFF262634),
)

private val LightColors = lightColorScheme(
    primary = GoldDim,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF6E9C9),
    onPrimaryContainer = Color(0xFF3A2D07),
    secondary = Color(0xFF0E8F88),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC7F2EE),
    onSecondaryContainer = Color(0xFF00201E),
    tertiary = Indigo,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE3E0FF),
    onTertiaryContainer = Color(0xFF1A1560),
    background = Color(0xFFF5F4FA),
    onBackground = Color(0xFF16151D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF16151D),
    surfaceVariant = Color(0xFFEAE8F2),
    onSurfaceVariant = Color(0xFF615F6E),
    outline = Color(0xFFC9C7D6),
    outlineVariant = Color(0xFFE4E2EE),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

val LocalIsDarkTheme = staticCompositionLocalOf { true }

@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            shapes = AppShapes,
            content = content,
        )
    }
}
