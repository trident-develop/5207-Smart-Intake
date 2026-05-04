package com.xd.smartintake.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xd.smartintake.ui.theme.PaleLime
import com.xd.smartintake.ui.theme.SoftGreenDark

@Composable
internal fun BottomNavigationBar(
    currentTab: Tab,
    onSelected: (Tab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier
            .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), clip = false)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Tab.entries.forEach { tab ->
            val selected = tab == currentTab
            val tint by animateColorAsState(
                if (selected) SoftGreenDark else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(220),
                label = "tint"
            )
            NavigationBarItem(
                selected = selected,
                onClick = { onSelected(tab) },
                icon = {
                    NavGlyph(
                        tab = tab,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        tab.title,
                        color = tint,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SoftGreenDark,
                    selectedTextColor = SoftGreenDark,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = PaleLime
                )
            )
        }
    }
}

@Composable
private fun NavGlyph(tab: Tab, tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = (w.coerceAtMost(h)) * 0.10f
        when (tab) {
            Tab.Today -> {
                val left = w * 0.14f
                val right = w * 0.86f
                val top = h * 0.22f
                val bottom = h * 0.86f
                val corner = w * 0.12f
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = left, top = top, right = right, bottom = bottom,
                            radiusX = corner, radiusY = corner
                        )
                    )
                }
                drawPath(path, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawLine(
                    tint,
                    Offset(left, top + h * 0.16f),
                    Offset(right, top + h * 0.16f),
                    stroke, StrokeCap.Round
                )
                drawLine(tint, Offset(w * 0.32f, h * 0.10f), Offset(w * 0.32f, h * 0.30f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.68f, h * 0.10f), Offset(w * 0.68f, h * 0.30f), stroke, StrokeCap.Round)
            }
            Tab.Manage -> {
                val lineLeft = w * 0.34f
                val lineRight = w * 0.86f
                val dotR = w * 0.06f
                val ys = listOf(0.27f, 0.50f, 0.73f)
                ys.forEach { yf ->
                    val y = h * yf
                    drawCircle(tint, dotR, Offset(w * 0.18f, y))
                    drawLine(tint, Offset(lineLeft, y), Offset(lineRight, y), stroke, StrokeCap.Round)
                }
            }
            Tab.Progress -> {
                val pad = stroke
                drawArc(
                    color = tint,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(pad, pad),
                    size = Size(w - 2 * pad, h - 2 * pad),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawCircle(tint, w * 0.08f, Offset(w * 0.50f, h * 0.50f))
            }
        }
    }
}
