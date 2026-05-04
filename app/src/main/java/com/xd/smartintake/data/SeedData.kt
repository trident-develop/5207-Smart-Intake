package com.xd.smartintake.data

import com.xd.smartintake.ui.theme.CapsuleA
import com.xd.smartintake.ui.theme.CapsuleB
import com.xd.smartintake.ui.theme.CapsuleC
import com.xd.smartintake.ui.theme.CapsuleD
import com.xd.smartintake.ui.theme.CapsuleE

private fun morning(time: String = TimeSlot.Morning.defaultTime) = mapOf(TimeSlot.Morning to time)
private fun afternoon(time: String = TimeSlot.Afternoon.defaultTime) = mapOf(TimeSlot.Afternoon to time)
private fun evening(time: String = TimeSlot.Evening.defaultTime) = mapOf(TimeSlot.Evening to time)

internal val SeedSupplements: List<Supplement> = listOf(
    Supplement(1, "Vitamin D", "1000 IU", morning("08:30"), 30, 18, CapsuleE),
    Supplement(2, "Omega 3", "1 capsule", morning("09:00"), null, 0, CapsuleA),
    Supplement(3, "Zinc", "15 mg", afternoon("13:00"), 14, 9, CapsuleC),
    Supplement(4, "Magnesium", "300 mg", evening("21:00"), 45, 12, CapsuleD),
    Supplement(5, "Collagen", "1 scoop", evening("21:30"), 60, 21, CapsuleB)
)
