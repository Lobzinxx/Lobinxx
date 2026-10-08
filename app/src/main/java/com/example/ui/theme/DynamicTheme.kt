package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.models.AppConfig

fun parseHexColor(hexString: String, fallback: Color): Color {
    return try {
        val clean = hexString.trim()
        val parsed = android.graphics.Color.parseColor(clean)
        Color(parsed)
    } catch (_: Exception) {
        fallback
    }
}

@Composable
fun DynamicAppTheme(
    config: AppConfig,
    content: @Composable () -> Unit
) {
    val primaryColor = parseHexColor(config.primaryColorHex, Color(0xFF6366F1))
    val secondaryColor = parseHexColor(config.secondaryColorHex, Color(0xFFEC4899))
    val backgroundColor = parseHexColor(config.backgroundColorHex, Color(0xFF0F172A))
    val surfaceColor = parseHexColor(config.surfaceColorHex, Color(0xFF1E293B))
    val textColor = parseHexColor(config.textColorHex, Color(0xFFF8FAFC))

    val colorScheme = darkColorScheme(
        primary = primaryColor,
        onPrimary = Color.White,
        primaryContainer = primaryColor.copy(alpha = 0.25f),
        onPrimaryContainer = Color.White,
        secondary = secondaryColor,
        onSecondary = Color.White,
        secondaryContainer = secondaryColor.copy(alpha = 0.25f),
        onSecondaryContainer = Color.White,
        background = backgroundColor,
        onBackground = textColor,
        surface = surfaceColor,
        onSurface = textColor,
        surfaceVariant = surfaceColor.copy(alpha = 0.85f),
        onSurfaceVariant = textColor.copy(alpha = 0.8f)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
