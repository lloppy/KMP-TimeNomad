package com.lloppy.timenomad.astro.planetaryhours

import com.lloppy.timenomad.astro.ephemeris.EphemerisService
import com.lloppy.timenomad.astro.math.AstroMath
import com.lloppy.timenomad.astro.math.AstroMath.acosDOrNull
import com.lloppy.timenomad.astro.math.AstroMath.atan2D
import com.lloppy.timenomad.astro.math.AstroMath.cosD
import com.lloppy.timenomad.astro.math.AstroMath.meanObliquity
import com.lloppy.timenomad.astro.math.AstroMath.norm180
import com.lloppy.timenomad.astro.math.AstroMath.sinD
import com.lloppy.timenomad.astro.math.AstroMath.tanD
import com.lloppy.timenomad.astro.model.CelestialBody
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.lloppy.timenomad.astro.time.AstroTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Один планетарный час: интервал и управитель. */
data class PlanetaryHour(
    val index: Int,
    val ruler: CelestialBody,
    val startUtcMillis: Long,
    val endUtcMillis: Long,
    val daytime: Boolean,
)

/** Результат расчёта планетарных часов вокруг заданного момента. */
data class PlanetaryHoursResult(
    val dayRuler: CelestialBody,
    val hours: List<PlanetaryHour>,
    val currentIndex: Int,
) {
    val current: PlanetaryHour? get() = hours.getOrNull(currentIndex)
}

/**
 * Планетарные дни и часы (халдейский ряд). Сутки идут от восхода до следующего восхода;
 * дневная/ночная части делятся на 12 неравных часов каждая.
 */
class PlanetaryHoursCalculator(private val ephemeris: EphemerisService) {

    fun calculate(momentUtcMillis: Long, latitude: Double, longitudeEast: Double): PlanetaryHoursResult? {
        val jd = AstroTime.julianDay(momentUtcMillis)
        val transit = solarTransit(jd, longitudeEast)
        var sunrise = solarEvent(transit, latitude, longitudeEast, rise = true) ?: return null
        var sunset = solarEvent(transit, latitude, longitudeEast, rise = false) ?: return null

        if (momentUtcMillis < AstroTime.epochMillis(sunrise)) {
            val prevTransit = solarTransit(jd - 1.0, longitudeEast)
            sunrise = solarEvent(prevTransit, latitude, longitudeEast, rise = true) ?: return null
            sunset = solarEvent(prevTransit, latitude, longitudeEast, rise = false) ?: return null
        }
        val nextTransit = solarTransit(AstroTime.julianDay(AstroTime.epochMillis(sunrise)) + 1.0, longitudeEast)
        val nextSunrise = solarEvent(nextTransit, latitude, longitudeEast, rise = true) ?: return null

        val sunriseMs = AstroTime.epochMillis(sunrise)
        val sunsetMs = AstroTime.epochMillis(sunset)
        val nextSunriseMs = AstroTime.epochMillis(nextSunrise)

        val dayRuler = dayRuler(sunriseMs)
        val startIndex = CHALDEAN.indexOf(dayRuler).coerceAtLeast(0)

        val dayHourLen = (sunsetMs - sunriseMs) / 12.0
        val nightHourLen = (nextSunriseMs - sunsetMs) / 12.0
        val hours = (0 until 24).map { i ->
            val daytime = i < 12
            val start = if (daytime) sunriseMs + (dayHourLen * i).toLong()
            else sunsetMs + (nightHourLen * (i - 12)).toLong()
            val end = if (daytime) sunriseMs + (dayHourLen * (i + 1)).toLong()
            else sunsetMs + (nightHourLen * (i - 11)).toLong()
            PlanetaryHour(
                index = i,
                ruler = CHALDEAN[(startIndex + i) % 7],
                startUtcMillis = start,
                endUtcMillis = end,
                daytime = daytime,
            )
        }
        val currentIndex = hours.indexOfFirst { momentUtcMillis in it.startUtcMillis until it.endUtcMillis }
            .coerceAtLeast(0)
        return PlanetaryHoursResult(dayRuler, hours, currentIndex)
    }

    private fun dayRuler(sunriseUtcMillis: Long): CelestialBody {
        val date = Instant.fromEpochMilliseconds(sunriseUtcMillis)
            .toLocalDateTime(TimeZone.UTC).date
        return when (date.dayOfWeek.ordinal) {
            0 -> CelestialBody.MOON
            1 -> CelestialBody.MARS
            2 -> CelestialBody.MERCURY
            3 -> CelestialBody.JUPITER
            4 -> CelestialBody.VENUS
            5 -> CelestialBody.SATURN
            else -> CelestialBody.SUN
        }
    }

    private data class RaDec(val ra: Double, val dec: Double)

    private fun sunRaDec(jd: Double): RaDec {
        val sun = ephemeris.position(jd, CelestialBody.SUN, ZodiacMode.Tropical)
        val eps = meanObliquity(AstroTime.julianCenturiesT(jd))
        val ra = AstroMath.norm360(
            atan2D(
                sinD(sun.longitude) * cosD(eps) - tanD(sun.latitude) * sinD(eps),
                cosD(sun.longitude),
            ),
        )
        return RaDec(ra, sun.declination)
    }

    /** JD верхней кульминации Солнца рядом с [jdGuess]. */
    private fun solarTransit(jdGuess: Double, longitudeEast: Double): Double {
        var jt = jdGuess
        repeat(5) {
            val ra = sunRaDec(jt).ra
            val lst = AstroTime.localSiderealTime(jt, longitudeEast)
            jt += norm180(ra - lst) / 360.985647
        }
        return jt
    }

    /** JD восхода/заката Солнца относительно кульминации [transitJd]. */
    private fun solarEvent(
        transitJd: Double,
        latitude: Double,
        longitudeEast: Double,
        rise: Boolean,
    ): Double? {
        val dec = sunRaDec(transitJd).dec
        val h0 = acosDOrNull(-tanD(latitude) * tanD(dec)) ?: return null
        val deltaDays = h0 / 360.985647
        return if (rise) transitJd - deltaDays else transitJd + deltaDays
    }

    private companion object {
        val CHALDEAN = listOf(
            CelestialBody.SATURN, CelestialBody.JUPITER, CelestialBody.MARS,
            CelestialBody.SUN, CelestialBody.VENUS, CelestialBody.MERCURY, CelestialBody.MOON,
        )
    }
}
