package com.xd.smartintake.ui.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xd.smartintake.data.Supplement
import com.xd.smartintake.data.TimeSlot
import com.xd.smartintake.data.slots
import com.xd.smartintake.ui.common.CheckGlyph
import com.xd.smartintake.ui.common.EntranceWrapper
import com.xd.smartintake.ui.common.ScreenHeader
import com.xd.smartintake.ui.common.SectionHeader
import com.xd.smartintake.ui.common.SoftCard
import com.xd.smartintake.ui.theme.PaleLime
import com.xd.smartintake.ui.theme.SoftGreen
import com.xd.smartintake.ui.theme.SoftGreenDark

@Composable
internal fun TodayScreen(
    supplements: SnapshotStateList<Supplement>,
    onTake: (Int, TimeSlot) -> Unit
) {
    val active = supplements.filter { it.active }
    val total = active.sumOf { it.slots.size }
    val taken = active.sumOf { it.takenSlots.size }
    val allDone = total > 0 && taken == total

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Today",
            subtitle = "Your daily supplement plan",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { DayProgressCard(taken = taken, total = total) }
            item {
                AnimatedVisibility(
                    visible = allDone,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AllDoneCard()
                }
            }
            TimeSlot.entries.forEach { slot ->
                val items = active.filter { slot in it.slots }
                if (items.isNotEmpty()) {
                    item(key = "header-${slot.name}") {
                        SectionHeader(
                            slot.label,
                            "${items.count { slot in it.takenSlots }}/${items.size}"
                        )
                    }
                    itemsIndexed(
                        items,
                        key = { _, s -> "${s.id}-${slot.name}" }
                    ) { index, supplement ->
                        EntranceWrapper(index = index) {
                            SupplementTodayCard(
                                supplement = supplement,
                                slot = slot,
                                onTake = { onTake(supplement.id, slot) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayProgressCard(taken: Int, total: Int) {
    val target = if (total == 0) 0f else taken.toFloat() / total.toFloat()
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "dayProgress"
    )
    SoftCard {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Today's progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "$taken of $total taken",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(PaleLime, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(animated * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = SoftGreenDark
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { animated },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
                color = SoftGreen,
                trackColor = PaleLime,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

@Composable
private fun AllDoneCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SoftGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CheckGlyph(modifier = Modifier.size(18.dp), tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    "All done for today.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Stay consistent — your body thanks you.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
