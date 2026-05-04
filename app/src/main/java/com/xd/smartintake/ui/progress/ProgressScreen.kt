package com.xd.smartintake.ui.progress

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xd.smartintake.data.Supplement
import com.xd.smartintake.data.scheduleText
import com.xd.smartintake.data.slots
import com.xd.smartintake.ui.common.CapsulePill
import com.xd.smartintake.ui.common.CheckGlyph
import com.xd.smartintake.ui.common.EntranceWrapper
import com.xd.smartintake.ui.common.ScreenHeader
import com.xd.smartintake.ui.common.SoftCard
import com.xd.smartintake.ui.common.lighten
import com.xd.smartintake.ui.theme.LimeAccent
import com.xd.smartintake.ui.theme.MintBackgroundDeep
import com.xd.smartintake.ui.theme.PaleLime
import com.xd.smartintake.ui.theme.SoftGreen
import com.xd.smartintake.ui.theme.SoftGreenDark

@Composable
internal fun ProgressScreen(supplements: SnapshotStateList<Supplement>) {
    val active = supplements.filter { it.active }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Progress",
            subtitle = "Daily and full-course progress",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
        )
        if (active.isEmpty()) {
            EmptyProgress()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(active, key = { _, s -> s.id }) { index, supplement ->
                    EntranceWrapper(index = index) {
                        SupplementProgressCard(supplement = supplement)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProgress() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Activate a supplement on Manage to start tracking progress.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SupplementProgressCard(supplement: Supplement) {
    val totalSlots = supplement.slots.size
    val takenCount = supplement.takenSlots.count { it in supplement.slots }
    val todayProgress = if (totalSlots == 0) 0f else takenCount.toFloat() / totalSlots.toFloat()

    val isOngoing = supplement.courseDays == null
    val courseProgress = if (!isOngoing && supplement.courseDays!! > 0) {
        (supplement.daysCompleted.toFloat() / supplement.courseDays.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    SoftCard {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CapsulePill(
                    modifier = Modifier.size(width = 44.dp, height = 22.dp),
                    leftColor = supplement.accent,
                    rightColor = lighten(supplement.accent, 0.25f)
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = supplement.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${supplement.dosage}  ·  ${supplement.scheduleText}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBlock(
                    progress = todayProgress,
                    isOngoing = false,
                    title = "Today",
                    sub = "$takenCount of $totalSlots taken",
                    modifier = Modifier.weight(1f)
                )
                MetricBlock(
                    progress = courseProgress,
                    isOngoing = isOngoing,
                    title = "Course",
                    sub = supplement.courseDays
                        ?.let { "${supplement.daysCompleted} / $it days" }
                        ?: "Ongoing",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Today's doses",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            SlotsTodayRow(supplement = supplement)
        }
    }
}

@Composable
private fun MetricBlock(
    progress: Float,
    isOngoing: Boolean,
    title: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MetricRing(
            progress = progress,
            isOngoing = isOngoing,
            modifier = Modifier.size(86.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = sub,
            style = MaterialTheme.typography.titleSmall,
            color = SoftGreenDark,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MetricRing(
    progress: Float,
    isOngoing: Boolean,
    modifier: Modifier = Modifier
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "metricRing"
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            val inset = stroke / 2
            drawArc(
                color = PaleLime,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - stroke, size.height - stroke),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            val sweepBrush = Brush.sweepGradient(listOf(SoftGreen, LimeAccent, SoftGreen))
            drawArc(
                brush = sweepBrush,
                startAngle = -90f,
                sweepAngle = if (isOngoing) 360f else 360f * animated,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - stroke, size.height - stroke),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        if (isOngoing) {
            Text(
                text = "∞",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = "${(animated * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SlotsTodayRow(supplement: Supplement) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        supplement.slots.sortedBy { it.ordinal }.forEach { slot ->
            val taken = slot in supplement.takenSlots
            SlotPill(label = slot.label, taken = taken)
        }
    }
}

@Composable
private fun SlotPill(label: String, taken: Boolean) {
    val bg by animateColorAsState(
        targetValue = if (taken) SoftGreen else MintBackgroundDeep,
        animationSpec = tween(280),
        label = "slotBg"
    )
    val fg by animateColorAsState(
        targetValue = if (taken) Color.White else SoftGreenDark,
        animationSpec = tween(280),
        label = "slotFg"
    )
    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (taken) {
            CheckGlyph(modifier = Modifier.size(12.dp), tint = Color.White)
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}
