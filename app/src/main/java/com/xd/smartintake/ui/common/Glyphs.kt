package com.xd.smartintake.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun CheckGlyph(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = (w.coerceAtMost(h)) * 0.16f
        val path = Path().apply {
            moveTo(w * 0.20f, h * 0.55f)
            lineTo(w * 0.45f, h * 0.78f)
            lineTo(w * 0.82f, h * 0.28f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
internal fun PlusGlyph(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val stroke = s * 0.16f
        val cx = size.width / 2f
        val cy = size.height / 2f
        drawLine(
            color = tint,
            start = Offset(cx, cy - s * 0.34f),
            end = Offset(cx, cy + s * 0.34f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(cx - s * 0.34f, cy),
            end = Offset(cx + s * 0.34f, cy),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
internal fun MinusGlyph(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val stroke = s * 0.16f
        val cx = size.width / 2f
        val cy = size.height / 2f
        drawLine(
            color = tint,
            start = Offset(cx - s * 0.34f, cy),
            end = Offset(cx + s * 0.34f, cy),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
internal fun CloseGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val stroke = s * 0.14f
        val a = s * 0.22f
        val b = s * 0.78f
        drawLine(tint, Offset(a, a), Offset(b, b), stroke, StrokeCap.Round)
        drawLine(tint, Offset(b, a), Offset(a, b), stroke, StrokeCap.Round)
    }
}

@Composable
internal fun TrashGlyph(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = (w.coerceAtMost(h)) * 0.10f
        drawLine(tint, Offset(w * 0.16f, h * 0.30f), Offset(w * 0.84f, h * 0.30f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.40f, h * 0.30f), Offset(w * 0.40f, h * 0.20f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.60f, h * 0.30f), Offset(w * 0.60f, h * 0.20f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.40f, h * 0.20f), Offset(w * 0.60f, h * 0.20f), stroke, StrokeCap.Round)
        val body = Path().apply {
            moveTo(w * 0.26f, h * 0.34f)
            lineTo(w * 0.32f, h * 0.84f)
            lineTo(w * 0.68f, h * 0.84f)
            lineTo(w * 0.74f, h * 0.34f)
        }
        drawPath(body, tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(tint, Offset(w * 0.42f, h * 0.44f), Offset(w * 0.42f, h * 0.76f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.58f, h * 0.44f), Offset(w * 0.58f, h * 0.76f), stroke, StrokeCap.Round)
    }
}

@Composable
internal fun EditGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = (w.coerceAtMost(h)) * 0.14f
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.78f),
            end = Offset(w * 0.70f, h * 0.30f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.18f, h * 0.82f),
            end = Offset(w * 0.30f, h * 0.92f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.70f, h * 0.30f),
            end = Offset(w * 0.86f, h * 0.46f),
            strokeWidth = stroke * 1.3f,
            cap = StrokeCap.Round
        )
    }
}
