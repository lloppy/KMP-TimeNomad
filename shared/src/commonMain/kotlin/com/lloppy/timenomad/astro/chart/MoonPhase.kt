package com.lloppy.timenomad.astro.chart

import com.lloppy.timenomad.astro.math.AstroMath
import kotlin.math.cos

enum class MoonPhaseName(val displayName: String) {
    NEW("Новолуние"),
    WAXING_CRESCENT("Растущий серп"),
    FIRST_QUARTER("Первая четверть"),
    WAXING_GIBBOUS("Растущая Луна"),
    FULL("Полнолуние"),
    WANING_GIBBOUS("Убывающая Луна"),
    LAST_QUARTER("Последняя четверть"),
    WANING_CRESCENT("Убывающий серп"),
}

data class MoonPhase(
    val angle: Double,
    val illumination: Double,
    val name: MoonPhaseName,
    val waxing: Boolean,
) {
    companion object {
        fun of(sunLongitude: Double, moonLongitude: Double): MoonPhase {
            val angle = AstroMath.norm360(moonLongitude - sunLongitude)
            val illumination = (1.0 - cos(angle * AstroMath.DEG2RAD)) / 2.0
            val name = when {
                angle < 22.5 || angle >= 337.5 -> MoonPhaseName.NEW
                angle < 67.5 -> MoonPhaseName.WAXING_CRESCENT
                angle < 112.5 -> MoonPhaseName.FIRST_QUARTER
                angle < 157.5 -> MoonPhaseName.WAXING_GIBBOUS
                angle < 202.5 -> MoonPhaseName.FULL
                angle < 247.5 -> MoonPhaseName.WANING_GIBBOUS
                angle < 292.5 -> MoonPhaseName.LAST_QUARTER
                else -> MoonPhaseName.WANING_CRESCENT
            }
            return MoonPhase(angle, illumination, name, waxing = angle < 180.0)
        }
    }
}
