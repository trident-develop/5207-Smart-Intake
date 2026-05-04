package com.xd.smartintake.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.json.JSONArray
import org.json.JSONObject

private const val PREFS_NAME = "smart_intake_prefs"
private const val KEY_SUPPLEMENTS = "supplements_v1"
private const val KEY_NEXT_ID = "next_id_v1"
private const val KEY_INITIALIZED = "initialized_v1"

internal class SupplementStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): Snapshot {
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            val seeded = SeedSupplements
            val nextId = seeded.maxOf { it.id } + 1
            save(seeded, nextId)
            prefs.edit().putBoolean(KEY_INITIALIZED, true).apply()
            return Snapshot(seeded, nextId)
        }
        val raw = prefs.getString(KEY_SUPPLEMENTS, null)
        val list = if (raw.isNullOrEmpty()) emptyList() else parseList(raw)
        val nextId = prefs.getInt(KEY_NEXT_ID, (list.maxOfOrNull { it.id } ?: 0) + 1)
        return Snapshot(list, nextId)
    }

    fun save(list: List<Supplement>, nextId: Int) {
        prefs.edit()
            .putString(KEY_SUPPLEMENTS, encodeList(list))
            .putInt(KEY_NEXT_ID, nextId)
            .apply()
    }

    private fun encodeList(list: List<Supplement>): String {
        val arr = JSONArray()
        list.forEach { arr.put(encode(it)) }
        return arr.toString()
    }

    private fun encode(s: Supplement): JSONObject = JSONObject().apply {
        put("id", s.id)
        put("name", s.name)
        put("dosage", s.dosage)
        put("slotTimes", slotTimesToJson(s.slotTimes))
        put("courseDays", s.courseDays ?: JSONObject.NULL)
        put("daysCompleted", s.daysCompleted)
        put("accent", s.accent.toArgb())
        put("active", s.active)
        put("takenSlots", slotsSetToJson(s.takenSlots))
    }

    private fun slotTimesToJson(map: Map<TimeSlot, String>): JSONObject {
        val obj = JSONObject()
        map.entries.sortedBy { it.key.ordinal }.forEach { (slot, time) ->
            obj.put(slot.name, time)
        }
        return obj
    }

    private fun slotsSetToJson(slots: Set<TimeSlot>): JSONArray {
        val arr = JSONArray()
        slots.sortedBy { it.ordinal }.forEach { arr.put(it.name) }
        return arr
    }

    private fun jsonToSlotsSet(arr: JSONArray): Set<TimeSlot> = buildSet {
        for (i in 0 until arr.length()) {
            runCatching { add(TimeSlot.valueOf(arr.getString(i))) }
        }
    }

    private fun parseList(raw: String): List<Supplement> = try {
        val arr = JSONArray(raw)
        List(arr.length()) { decode(arr.getJSONObject(it)) }
    } catch (_: Exception) {
        emptyList()
    }

    private fun decode(o: JSONObject): Supplement {
        val slotTimes: Map<TimeSlot, String> = when {
            o.has("slotTimes") -> {
                val obj = o.getJSONObject("slotTimes")
                buildMap {
                    obj.keys().forEach { key ->
                        runCatching {
                            val slot = TimeSlot.valueOf(key)
                            put(slot, obj.getString(key))
                        }
                    }
                }
            }
            o.has("slots") -> {
                jsonToSlotsSet(o.getJSONArray("slots"))
                    .associateWith { it.defaultTime }
            }
            o.has("slot") -> {
                val slot = TimeSlot.valueOf(o.getString("slot"))
                mapOf(slot to slot.defaultTime)
            }
            else -> mapOf(TimeSlot.Morning to TimeSlot.Morning.defaultTime)
        }.ifEmpty { mapOf(TimeSlot.Morning to TimeSlot.Morning.defaultTime) }

        val takenSlots: Set<TimeSlot> = when {
            o.has("takenSlots") -> jsonToSlotsSet(o.getJSONArray("takenSlots"))
            o.optBoolean("taken", false) -> slotTimes.keys
            else -> emptySet()
        }

        return Supplement(
            id = o.getInt("id"),
            name = o.getString("name"),
            dosage = o.getString("dosage"),
            slotTimes = slotTimes,
            courseDays = if (o.isNull("courseDays")) null else o.getInt("courseDays"),
            daysCompleted = o.optInt("daysCompleted", 0),
            accent = Color(o.getInt("accent")),
            active = o.optBoolean("active", true),
            takenSlots = takenSlots
        )
    }

    internal data class Snapshot(val list: List<Supplement>, val nextId: Int)
}
