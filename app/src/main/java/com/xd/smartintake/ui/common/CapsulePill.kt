package com.xd.smartintake.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

@Composable
internal fun CapsulePill(
    modifier: Modifier = Modifier,
    leftColor: Color,
    rightColor: Color,
    alpha: Float = 1f
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .graphicsLayer { this.alpha = alpha }
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(leftColor)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(rightColor)
        )
    }
}
