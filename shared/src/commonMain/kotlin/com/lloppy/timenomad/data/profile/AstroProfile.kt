package com.lloppy.timenomad.data.profile

import com.lloppy.timenomad.astro.time.AstroTime
import kotlinx.serialization.Serializable

@Serializable
data class AstroProfile(
    val id: String,
    val name: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val utcOffsetMinutes: Int,
    val latitude: Double,
    val longitude: Double,
    val placeLabel: String = "",
    val notes: String = "",
    val tags: List<String> = emptyList(),
) {
    val epochUtcMillis: Long
        get() = AstroTime.epochMillisFromLocal(
            AstroTime.localDateTime(year, month, day, hour, minute),
            utcOffsetMinutes,
        )

    val birthDateLabel: String
        get() = "${pad(day)}.${pad(month)}.$year ${pad(hour)}:${pad(minute)}"

    private fun pad(v: Int): String = if (v < 10) "0$v" else v.toString()
}
