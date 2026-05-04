package com.xd.smartintake.data

import androidx.compose.ui.graphics.Color
import com.xd.smartintake.ui.theme.CapsuleA

internal enum class TimeSlot(val label: String, val defaultTime: String) {
    Morning("Morning", "08:30"),
    Afternoon("Afternoon", "13:00"),
    Evening("Evening", "21:00")
}

internal data class Supplement(
    val id: Int,
    val name: String,
    val dosage: String,
    val slotTimes: Map<TimeSlot, String>,
    val courseDays: Int?, // null => ongoing
    val daysCompleted: Int = 0,
    val accent: Color = CapsuleA,
    val active: Boolean = true,
    val takenSlots: Set<TimeSlot> = emptySet()
)

internal val Supplement.slots: Set<TimeSlot>
    get() = slotTimes.keys

internal fun Supplement.timeFor(slot: TimeSlot): String =
    slotTimes[slot] ?: slot.defaultTime

internal val Supplement.scheduleText: String
    get() = slotTimes.keys.sortedBy { it.ordinal }.joinToString(" · ") { it.label }
