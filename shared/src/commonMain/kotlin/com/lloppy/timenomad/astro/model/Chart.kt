package com.lloppy.timenomad.astro.model

import kotlin.jvm.JvmInline

enum class HouseSystem(val displayName: String) {
    PLACIDUS("Плацидус"),
    KOCH("Кох"),
    PORPHYRY("Порфирий"),
    EQUAL("Равнодомная"),
    WHOLE_SIGN("Целый знак"),
}

enum class Ayanamsha(val displayName: String) {
    LAHIRI("Лахири"),
    FAGAN_BRADLEY("Фейган—Брэдли"),
}

sealed interface ZodiacMode {
    data object Tropical : ZodiacMode
    data class Sidereal(val ayanamsha: Ayanamsha = Ayanamsha.LAHIRI) : ZodiacMode

    val label: String
        get() = when (this) {
            Tropical -> "Тропический"
            is Sidereal -> "Сидерический (${ayanamsha.displayName})"
        }
}

@JvmInline
value class Longitude(val degrees: Double) {
    val sign: ZodiacSign get() = ZodiacSign.fromLongitude(degrees)
    val degreeInSign: Double get() = (((degrees % 360.0) + 360.0) % 360.0) % 30.0

    companion object {
        fun of(value: Double) = Longitude(((value % 360.0) + 360.0) % 360.0)
    }
}

data class PlanetPosition(
    val body: CelestialBody,
    val longitude: Double,
    val latitude: Double,
    val distanceAu: Double,
    val speedLongitude: Double,
    val declination: Double = 0.0,
) {
    val sign: ZodiacSign get() = ZodiacSign.fromLongitude(longitude)
    val degreeInSign: Double get() = (((longitude % 360.0) + 360.0) % 360.0) % 30.0
    val retrograde: Boolean get() = body.canRetrograde && speedLongitude < 0.0
}

data class House(val number: Int, val cusp: Double) {
    val sign: ZodiacSign get() = ZodiacSign.fromLongitude(cusp)
}

enum class ChartKind { NATAL, TRANSIT, SYNASTRY, COMPOSITE, PROGRESSED }

data class Chart(
    val kind: ChartKind,
    val momentUtcMillis: Long,
    val julianDayUt: Double,
    val latitude: Double,
    val longitude: Double,
    val zodiacMode: ZodiacMode,
    val houseSystem: HouseSystem,
    val positions: List<PlanetPosition>,
    val houses: List<House>,
    val ascendant: Double,
    val midheaven: Double,
    val aspects: List<AspectHit>,
) {
    fun position(body: CelestialBody): PlanetPosition? = positions.firstOrNull { it.body == body }

    fun houseOf(longitude: Double): Int {
        if (houses.size != 12) return 0
        val lon = ((longitude % 360.0) + 360.0) % 360.0
        for (i in 0 until 12) {
            val start = houses[i].cusp
            val end = houses[(i + 1) % 12].cusp
            if (inArc(lon, start, end)) return houses[i].number
        }
        return 0
    }

    private fun inArc(value: Double, start: Double, end: Double): Boolean {
        val span = ((end - start) % 360.0 + 360.0) % 360.0
        val offset = ((value - start) % 360.0 + 360.0) % 360.0
        return offset < span
    }
}
