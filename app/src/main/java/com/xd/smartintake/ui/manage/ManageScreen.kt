package com.xd.smartintake.ui.manage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xd.smartintake.data.Supplement
import com.xd.smartintake.data.scheduleText
import com.xd.smartintake.ui.common.CapsulePill
import com.xd.smartintake.ui.common.EntranceWrapper
import com.xd.smartintake.ui.common.ScreenHeader
import com.xd.smartintake.ui.common.SoftCard
import com.xd.smartintake.ui.common.TrashGlyph
import com.xd.smartintake.ui.common.lighten
import com.xd.smartintake.ui.theme.DangerSoft
import com.xd.smartintake.ui.theme.MintBackgroundDeep
import com.xd.smartintake.ui.theme.OutlineSoft
import com.xd.smartintake.ui.theme.SoftGreen
import com.xd.smartintake.ui.theme.SoftGreenDark

@Composable
internal fun ManageScreen(
    supplements: SnapshotStateList<Supplement>,
    onToggleActive: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Manage",
            subtitle = "Set up your supplement routine",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            itemsIndexed(supplements, key = { _, s -> s.id }) { index, supplement ->
                EntranceWrapper(index = index) {
                    SwipeToDeleteWrapper(onDelete = { onDelete(supplement.id) }) {
                        ManageCard(
                            supplement = supplement,
                            onToggle = { onToggleActive(supplement.id) }
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteWrapper(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance -> distance * 0.5f }
    )
    LaunchedEffect(state.currentValue) {
        if (state.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DangerSoft),
                contentAlignment = Alignment.CenterEnd
            ) {
                TrashGlyph(
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .size(22.dp),
                    tint = Color.White
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun ManageCard(
    supplement: Supplement,
    onToggle: () -> Unit
) {
    val containerAlpha by animateFloatAsState(
        if (supplement.active) 1f else 0.62f,
        animationSpec = tween(260),
        label = "manageAlpha"
    )
    SoftCard {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .graphicsLayer { alpha = containerAlpha }
        ) {
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
                        text = supplement.dosage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = supplement.active,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SoftGreen,
                        checkedBorderColor = SoftGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = OutlineSoft,
                        uncheckedBorderColor = OutlineSoft
                    )
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetaChip(text = supplement.scheduleText)
                MetaChip(
                    text = supplement.courseDays?.let { "$it-day course" } ?: "Ongoing"
                )
            }
        }
    }
}

@Composable
private fun MetaChip(text: String) {
    Box(
        modifier = Modifier
            .background(MintBackgroundDeep, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = SoftGreenDark
        )
    }
}
