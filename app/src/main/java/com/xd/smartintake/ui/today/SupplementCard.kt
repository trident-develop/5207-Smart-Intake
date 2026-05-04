package com.xd.smartintake.ui.today

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xd.smartintake.data.Supplement
import com.xd.smartintake.data.TimeSlot
import com.xd.smartintake.data.timeFor
import com.xd.smartintake.ui.common.CapsulePill
import com.xd.smartintake.ui.common.CheckGlyph
import com.xd.smartintake.ui.common.SoftCard
import com.xd.smartintake.ui.common.lighten
import com.xd.smartintake.ui.theme.PaleLime
import com.xd.smartintake.ui.theme.SoftGreen
import com.xd.smartintake.ui.theme.SoftGreenDark

@Composable
internal fun SupplementCard(
    supplement: Supplement,
    time: String,
    muted: Boolean,
    trailing: @Composable () -> Unit
) {
    val containerAlpha by animateFloatAsState(
        if (muted) 0.78f else 1f,
        animationSpec = tween(300),
        label = "muted"
    )
    SoftCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .graphicsLayer { alpha = containerAlpha },
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${supplement.dosage}  ·  $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trailing()
        }
    }
}

@Composable
internal fun SupplementTodayCard(
    supplement: Supplement,
    slot: TimeSlot,
    onTake: () -> Unit
) {
    val taken = slot in supplement.takenSlots
    SupplementCard(
        supplement = supplement,
        time = supplement.timeFor(slot),
        muted = taken
    ) {
        TakeButton(taken = taken, onClick = onTake)
    }
}

@Composable
private fun TakeButton(taken: Boolean, onClick: () -> Unit) {
    val container by animateColorAsState(
        if (taken) PaleLime else SoftGreen,
        animationSpec = tween(280),
        label = "btnBg"
    )
    val content by animateColorAsState(
        if (taken) SoftGreenDark else Color.White,
        animationSpec = tween(280),
        label = "btnFg"
    )
    Button(
        onClick = onClick,
        enabled = !taken,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container,
            disabledContentColor = content
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        AnimatedContent(
            targetState = taken,
            label = "takenContent",
            transitionSpec = {
                (scaleIn(tween(220)) + fadeIn(tween(220)))
                    .togetherWith(fadeOut(tween(140)))
            }
        ) { isTaken ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isTaken) {
                    CheckGlyph(modifier = Modifier.size(14.dp), tint = SoftGreenDark)
                    Spacer(Modifier.width(6.dp))
                    Text("Taken", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                } else {
                    Text("Take", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

