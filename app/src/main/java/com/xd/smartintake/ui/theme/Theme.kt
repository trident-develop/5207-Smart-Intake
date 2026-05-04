package com.xd.smartintake.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WellnessColorScheme = lightColorScheme(
    primary = SoftGreen,
    onPrimary = CardWhite,
    primaryContainer = PaleLime,
    onPrimaryContainer = SoftGreenDark,
    secondary = LimeAccent,
    onSecondary = DarkGreenText,
    secondaryContainer = PaleLime,
    onSecondaryContainer = DarkGreenText,
    tertiary = SoftGreenDark,
    onTertiary = CardWhite,
    background = MintBackground,
    onBackground = DarkGreenText,
    surface = CardWhite,
    onSurface = DarkGreenText,
    surfaceVariant = MintBackgroundDeep,
    onSurfaceVariant = MutedGreenText,
    outline = OutlineSoft,
    outlineVariant = OutlineSoft,
    error = DangerSoft,
    onError = CardWhite
)

@Composable
fun SmartIntakeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WellnessColorScheme,
        typography = Typography,
        content = content
    )
}
