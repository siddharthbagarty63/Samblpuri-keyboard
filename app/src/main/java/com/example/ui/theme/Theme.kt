package com.example.ui.theme

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

private val DarkColorScheme =
    darkColorScheme(
        primary = SambalpuriGold,
        onPrimary = SambalpuriCrimsonDark,
        primaryContainer = SambalpuriCrimson,
        onPrimaryContainer = SambalpuriGoldLight,
        secondary = SambalpuriPeacockLight,
        onSecondary = Color(0xFF003730),
        secondaryContainer = SambalpuriPeacock,
        onSecondaryContainer = Color(0xFFE0F2F1),
        tertiary = SambalpuriMarigold,
        background = SambalpuriDarkBg,
        onBackground = Color(0xFFEDE0D4),
        surface = SambalpuriDarkSurface,
        onSurface = Color(0xFFEDE0D4),
        surfaceVariant = SambalpuriDarkCard,
        onSurfaceVariant = Color(0xFFD7CCC8)
    )

private val LightColorScheme =
    lightColorScheme(
        primary = SambalpuriCrimson,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDBCF),
        onPrimaryContainer = SambalpuriCrimsonDark,
        secondary = SambalpuriPeacock,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCCE8E4),
        onSecondaryContainer = Color(0xFF00201C),
        tertiary = SambalpuriMarigold,
        background = SambalpuriCream,
        onBackground = Color(0xFF2C2420),
        surface = Color.White,
        onSurface = Color(0xFF2C2420),
        surfaceVariant = SambalpuriSurfaceLight,
        onSurfaceVariant = Color(0xFF5D4037)
    )

@Composable
fun SambalpuriKeyboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve distinctive Sambalpuri cultural styling by default
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
