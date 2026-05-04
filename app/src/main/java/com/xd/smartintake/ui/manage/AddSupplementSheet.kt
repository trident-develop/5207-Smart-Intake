package com.xd.smartintake.ui.manage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xd.smartintake.data.TimeSlot
import com.xd.smartintake.ui.common.CloseGlyph
import com.xd.smartintake.ui.common.MinusGlyph
import com.xd.smartintake.ui.common.PlusGlyph
import com.xd.smartintake.ui.theme.MintBackground
import com.xd.smartintake.ui.theme.OutlineSoft
import com.xd.smartintake.ui.theme.SoftGreen
import com.xd.smartintake.ui.theme.SoftGreenDark
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddSupplementSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, dosage: String, slotTimes: Map<TimeSlot, String>, courseDays: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
        focusManager.clearFocus(force = true)
    }
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var slotTimes by remember {
        mutableStateOf(mapOf(TimeSlot.Morning to TimeSlot.Morning.defaultTime))
    }
    var courseDays by remember { mutableIntStateOf(30) }
    var editingSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var nameFocused by remember { mutableStateOf(false) }
    var dosageFocused by remember { mutableStateOf(false) }

    val canSave by remember {
        derivedStateOf { name.trim().isNotEmpty() && dosage.trim().isNotEmpty() }
    }

    LaunchedEffect(nameFocused, dosageFocused) {
        if (!nameFocused && !dosageFocused) {
            delay(80)
            if (!nameFocused && !dosageFocused) dismissKeyboard()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(OutlineSoft, RoundedCornerShape(50))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { dismissKeyboard() })
                }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(top = 8.dp, bottom = 28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Add supplement",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { dismissKeyboard(); onDismiss() }) {
                    CloseGlyph(modifier = Modifier.size(18.dp), tint = SoftGreenDark)
                }
            }
            Text(
                text = "Plan a new supplement in your routine.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            FieldLabel("Supplement name")
            WellnessField(
                value = name,
                onValueChange = { name = it },
                placeholder = "e.g. Vitamin C",
                imeAction = ImeAction.Done,
                onImeAction = dismissKeyboard,
                modifier = Modifier.onFocusChanged { nameFocused = it.isFocused }
            )
            Spacer(Modifier.height(14.dp))

            FieldLabel("Dosage")
            WellnessField(
                value = dosage,
                onValueChange = { input -> dosage = input.filter { it.isDigit() } },
                placeholder = "e.g. 500",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onImeAction = dismissKeyboard,
                modifier = Modifier.onFocusChanged { dosageFocused = it.isFocused }
            )
            Spacer(Modifier.height(18.dp))

            FieldLabel("Time of day")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeSlot.entries.forEach { ts ->
                    val isSelected = ts in slotTimes
                    SelectionChip(
                        text = ts.label,
                        selected = isSelected,
                        onClick = {
                            dismissKeyboard()
                            slotTimes = when {
                                !isSelected -> slotTimes + (ts to ts.defaultTime)
                                slotTimes.size > 1 -> slotTimes - ts
                                else -> slotTimes
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                slotTimes.entries.sortedBy { it.key.ordinal }.forEach { (slot, time) ->
                    SlotTimeRow(
                        slot = slot,
                        time = time,
                        onTap = {
                            dismissKeyboard()
                            editingSlot = slot
                        }
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            FieldLabel("Course duration")
            DayStepper(
                value = courseDays,
                onChange = { dismissKeyboard(); courseDays = it }
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    dismissKeyboard()
                    if (canSave) onSave(name.trim(), dosage.trim(), slotTimes, courseDays)
                },
                enabled = canSave,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftGreen,
                    contentColor = Color.White,
                    disabledContainerColor = OutlineSoft,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Save supplement", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }

    val slotBeingEdited = editingSlot
    if (slotBeingEdited != null) {
        TimePickerSheet(
            initial = slotTimes[slotBeingEdited] ?: slotBeingEdited.defaultTime,
            onDismiss = { editingSlot = null },
            onConfirm = { newTime ->
                slotTimes = slotTimes + (slotBeingEdited to newTime)
                editingSlot = null
            }
        )
    }
}

@Composable
private fun SlotTimeRow(
    slot: TimeSlot,
    time: String,
    onTap: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MintBackground, RoundedCornerShape(14.dp))
            .clickable(onClick = onTap)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = slot.label,
            style = MaterialTheme.typography.titleSmall,
            color = SoftGreenDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .background(SoftGreen, RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerSheet(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val (initH, initM) = parseTime(initial)
    val state = rememberTimePickerState(
        initialHour = initH,
        initialMinute = initM,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Pick a time",
                style = MaterialTheme.typography.titleLarge,
                color = SoftGreenDark,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = state)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(formatTime(state.hour, state.minute)) }) {
                Text("Save", color = SoftGreen, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

private fun parseTime(raw: String): Pair<Int, Int> {
    val parts = raw.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 8
    val m = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
    return h to m
}

private fun formatTime(h: Int, m: Int): String =
    "%02d:%02d".format(h, m)

@Composable
private fun DayStepper(
    value: Int,
    onChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MintBackground, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton(
            enabled = value > 1,
            onClick = { onChange((value - 1).coerceAtLeast(1)) }
        ) { enabled ->
            MinusGlyph(
                modifier = Modifier.size(18.dp),
                tint = if (enabled) Color.White else SoftGreenDark.copy(alpha = 0.4f)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = value,
                label = "dayCount",
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically(tween(180)) { it / 3 } + fadeIn(tween(180)))
                            .togetherWith(slideOutVertically(tween(140)) { -it / 3 } + fadeOut(tween(140)))
                    } else {
                        (slideInVertically(tween(180)) { -it / 3 } + fadeIn(tween(180)))
                            .togetherWith(slideOutVertically(tween(140)) { it / 3 } + fadeOut(tween(140)))
                    }
                }
            ) { current ->
                Text(
                    text = "$current ${if (current == 1) "day" else "days"}",
                    style = MaterialTheme.typography.titleLarge,
                    color = SoftGreenDark,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
        StepperButton(
            enabled = true,
            onClick = { onChange(value + 1) }
        ) { enabled ->
            PlusGlyph(
                modifier = Modifier.size(18.dp),
                tint = if (enabled) Color.White else SoftGreenDark.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun StepperButton(
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable (enabled: Boolean) -> Unit
) {
    val container by animateColorAsState(
        targetValue = if (enabled) SoftGreen else OutlineSoft,
        animationSpec = tween(220),
        label = "stepperBg"
    )
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(container, CircleShape)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        content(enabled)
    }
}
