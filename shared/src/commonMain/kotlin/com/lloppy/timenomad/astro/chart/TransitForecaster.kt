package com.lloppy.timenomad.astro.chart

import com.lloppy.timenomad.astro.ephemeris.EphemerisService
import com.lloppy.timenomad.astro.math.AstroMath.norm180
import com.lloppy.timenomad.astro.model.AspectType
import com.lloppy.timenomad.astro.model.CelestialBody
import com.lloppy.timenomad.astro.model.PlanetPosition
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.lloppy.timenomad.astro.model.ZodiacSign
import com.lloppy.timenomad.astro.time.AstroTime

data class MoonIngress(val momentUtcMillis: Long, val sign: ZodiacSign)

data class TransitContact(
    val momentUtcMillis: Long,
    val transiting: CelestialBody,
    val natal: CelestialBody,
    val type: AspectType,
)

/**
 * Прогноз ближайших лунных событий: смены знака (ингрессии) и соединения транзитной Луны
 * с натальными точками («пиковые дни»). Поиск — сканирование вперёд с уточнением момента бисекцией.
 */
class TransitForecaster(private val ephemeris: EphemerisService) {

    fun moonIngresses(fromMillis: Long, mode: ZodiacMode, count: Int): List<MoonIngress> {
        val result = mutableListOf<MoonIngress>()
        var prev = fromMillis
        var prevSign = signIndex(moonLon(prev, mode))
        var t = fromMillis + STEP_MS
        val limit = fromMillis + (count + 2).toLong() * 3L * DAY_MS
        while (result.size < count && t < limit) {
            val sign = signIndex(moonLon(t, mode))
            if (sign != prevSign) {
                val boundary = sign * 30.0
                val moment = zeroCrossing(prev, t) { norm180(moonLon(it, mode) - boundary) }
                result.add(MoonIngress(moment, ZodiacSign.entries[sign]))
                prevSign = sign
            }
            prev = t
            t += STEP_MS
        }
        return result
    }

    fun moonConjunctions(
        natal: List<PlanetPosition>,
        fromMillis: Long,
        mode: ZodiacMode,
        horizonDays: Int,
    ): List<TransitContact> {
        val result = mutableListOf<TransitContact>()
        val end = fromMillis + horizonDays.toLong() * DAY_MS
        for (point in natal) {
            var prev = fromMillis
            var gPrev = norm180(moonLon(prev, mode) - point.longitude)
            var t = fromMillis + CONTACT_STEP_MS
            while (t <= end) {
                val g = norm180(moonLon(t, mode) - point.longitude)
                if (signOf(gPrev) != signOf(g) && kotlin.math.abs(gPrev) < 90.0 && kotlin.math.abs(g) < 90.0) {
                    val moment = zeroCrossing(prev, t) { norm180(moonLon(it, mode) - point.longitude) }
                    result.add(TransitContact(moment, CelestialBody.MOON, point.body, AspectType.CONJUNCTION))
                }
                prev = t
                gPrev = g
                t += CONTACT_STEP_MS
            }
        }
        return result.sortedBy { it.momentUtcMillis }
    }

    private fun moonLon(millis: Long, mode: ZodiacMode): Double =
        ephemeris.position(AstroTime.julianDay(millis), CelestialBody.MOON, mode).longitude

    private fun signIndex(longitude: Double): Int = (longitude / 30.0).toInt().coerceIn(0, 11)

    private fun signOf(v: Double): Int = if (v >= 0) 1 else -1

    private inline fun zeroCrossing(lo: Long, hi: Long, f: (Long) -> Double): Long {
        var a = lo
        var b = hi
        repeat(28) {
            val mid = a + (b - a) / 2
            if (signOf(f(a)) == signOf(f(mid))) a = mid else b = mid
        }
        return a + (b - a) / 2
    }

    private companion object {
        const val DAY_MS = 86_400_000L
        const val STEP_MS = 3_600_000L
        const val CONTACT_STEP_MS = 7_200_000L
    }
}
