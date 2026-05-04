package com.xd.smartintake.ui.common

import androidx.compose.ui.graphics.Color

internal fun lighten(color: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = color.red + (1f - color.red) * f,
        green = color.green + (1f - color.green) * f,
        blue = color.blue + (1f - color.blue) * f,
        alpha = color.alpha
    )
}
