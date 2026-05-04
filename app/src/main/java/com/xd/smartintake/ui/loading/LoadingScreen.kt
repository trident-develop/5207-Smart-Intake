package com.xd.smartintake.ui.loading

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.xd.smartintake.ui.common.CapsulePill
import com.xd.smartintake.ui.theme.CapsuleA
import com.xd.smartintake.ui.theme.CapsuleB
import com.xd.smartintake.ui.theme.CapsuleC
import com.xd.smartintake.ui.theme.CapsuleE
import com.xd.smartintake.ui.theme.LimeAccent
import com.xd.smartintake.ui.theme.MintBackground
import com.xd.smartintake.ui.theme.MintBackgroundDeep
import com.xd.smartintake.ui.theme.PaleLime
import com.xd.smartintake.ui.theme.SoftGreen
import com.xd.smartintake.ui.theme.SoftGreenDark

@Composable
internal fun LoadingScreen() {
    val infinite = rememberInfiniteTransition(label = "loading")
    val rotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing)),
        label = "rotation"
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(MintBackground, MintBackgroundDeep, PaleLime))
            ),
        contentAlignment = Alignment.Center
    ) {
        FloatingCapsulesBackdrop()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                ) {
                    val radius = size.minDimension / 2f - 14f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dotPositions = 8
                    for (idx in 0 until dotPositions) {
                        val angle = (Math.PI * 2 * idx / dotPositions).toFloat()
                        val px = center.x + radius * kotlin.math.cos(angle)
                        val py = center.y + radius * kotlin.math.sin(angle)
                        val r = if (idx % 2 == 0) 6f else 4f
                        val color = if (idx % 2 == 0) SoftGreen else LimeAccent
                        drawCircle(color = color.copy(alpha = 0.85f), radius = r, center = Offset(px, py))
                    }
                }
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(pulse)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(SoftGreen.copy(alpha = 0.18f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
                CapsulePill(
                    modifier = Modifier
                        .size(width = 96.dp, height = 44.dp)
                        .scale(pulse)
                        .shadow(12.dp, RoundedCornerShape(50)),
                    leftColor = SoftGreen,
                    rightColor = PaleLime
                )
            }

            Spacer(Modifier.height(36.dp))

            Text(
                text = "Smart Intake",
                style = MaterialTheme.typography.headlineLarge,
                color = SoftGreenDark
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Wellness in every dose",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            InfiniteThreeDotIndicator()
        }
    }
}

@Composable
private fun FloatingCapsulesBackdrop() {
    val infinite = rememberInfiniteTransition(label = "floaters")
    val drift1 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "d1"
    )
    val drift2 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "d2"
    )
    val drift3 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "d3"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CapsulePill(
            modifier = Modifier
                .padding(start = 32.dp, top = 110.dp)
                .size(width = 70.dp, height = 28.dp)
                .graphicsLayer {
                    translationY = -18.dp.toPx() * drift1
                    rotationZ = -18f + 8f * drift1
                },
            leftColor = CapsuleA, rightColor = PaleLime, alpha = 0.55f
        )
        CapsulePill(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 28.dp, top = 160.dp)
                .size(width = 60.dp, height = 24.dp)
                .graphicsLayer {
                    translationY = 22.dp.toPx() * drift2
                    rotationZ = 22f - 10f * drift2
                },
            leftColor = CapsuleE, rightColor = LimeAccent, alpha = 0.55f
        )
        CapsulePill(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 150.dp)
                .size(width = 80.dp, height = 32.dp)
                .graphicsLayer {
                    translationY = -16.dp.toPx() * drift3
                    rotationZ = 30f - 12f * drift3
                },
            leftColor = SoftGreen, rightColor = CapsuleC, alpha = 0.55f
        )
        CapsulePill(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 36.dp, bottom = 110.dp)
                .size(width = 56.dp, height = 22.dp)
                .graphicsLayer {
                    translationY = 18.dp.toPx() * drift1
                    rotationZ = -24f + 10f * drift1
                },
            leftColor = CapsuleB, rightColor = PaleLime, alpha = 0.55f
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val pts = listOf(
                Offset(w * 0.2f, h * 0.3f) to 4f,
                Offset(w * 0.78f, h * 0.42f) to 5f,
                Offset(w * 0.32f, h * 0.78f) to 3f,
                Offset(w * 0.68f, h * 0.82f) to 4f,
                Offset(w * 0.12f, h * 0.55f) to 3f,
                Offset(w * 0.88f, h * 0.62f) to 4f
            )
            for ((p, r) in pts) {
                drawCircle(color = SoftGreen.copy(alpha = 0.18f), radius = r, center = p)
            }
        }
    }
}

@Composable
private fun InfiniteThreeDotIndicator() {
    val infinite = rememberInfiniteTransition(label = "dots")
    val animValues = (0 until 3).map { i ->
        infinite.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, delayMillis = i * 150, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot$i"
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        animValues.forEach { state ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .size(10.dp)
                    .scale(state.value)
                    .background(SoftGreen.copy(alpha = state.value), CircleShape)
            )
        }
    }
}
