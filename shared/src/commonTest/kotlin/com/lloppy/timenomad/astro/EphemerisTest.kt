package com.lloppy.timenomad.astro

import com.lloppy.timenomad.astro.ephemeris.MeeusEphemeris
import com.lloppy.timenomad.astro.math.AstroMath
import com.lloppy.timenomad.astro.model.CelestialBody
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.lloppy.timenomad.astro.model.Ayanamsha
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Проверки физической вменяемости движка эфемерид: ловят грубые опечатки в коэффициентах.
 * Допуски широкие — это санити-тесты, не сверка с эталоном до угловой секунды.
 */
class EphemerisTest {

    private val eph = MeeusEphemeris()
    private val j2000 = 2451545.0

    private fun lon(jd: Double, body: CelestialBody) =
        eph.position(jd, body, ZodiacMode.Tropical).longitude

    @Test
    fun sunNearJ2000IsAround280() {
        val l = lon(j2000, CelestialBody.SUN)
        assertTrue(abs(AstroMath.norm180(l - 280.46)) < 0.5, "Sun@J2000 = $l, ожидалось ~280.46")
    }

    @Test
    fun sunAtSpringEquinoxIsNearZero() {
        val jd = 2451624.0
        val l = lon(jd, CelestialBody.SUN)
        val dist = minOf(l, 360.0 - l)
        assertTrue(dist < 1.5, "Sun@equinox = $l, ожидалось ~0")
    }

    @Test
    fun sunDailySpeedIsAboutOneDegree() {
        val s = eph.position(j2000, CelestialBody.SUN, ZodiacMode.Tropical).speedLongitude
        assertTrue(s in 0.95..1.02, "Скорость Солнца = $s °/сут")
    }

    @Test
    fun moonDailySpeedIsAboutThirteenDegrees() {
        val s = eph.position(j2000, CelestialBody.MOON, ZodiacMode.Tropical).speedLongitude
        assertTrue(s in 11.0..15.5, "Скорость Луны = $s °/сут")
    }

    @Test
    fun mercuryStaysNearSun() {
        repeat(8) { k ->
            val jd = j2000 + k * 40.0
            val sep = abs(AstroMath.norm180(lon(jd, CelestialBody.MERCURY) - lon(jd, CelestialBody.SUN)))
            assertTrue(sep <= 30.0, "Элонгация Меркурия = $sep° на jd=$jd")
        }
    }

    @Test
    fun venusStaysNearSun() {
        repeat(8) { k ->
            val jd = j2000 + k * 40.0
            val sep = abs(AstroMath.norm180(lon(jd, CelestialBody.VENUS) - lon(jd, CelestialBody.SUN)))
            assertTrue(sep <= 48.0, "Элонгация Венеры = $sep° на jd=$jd")
        }
    }

    @Test
    fun lahiriAyanamshaAtJ2000IsAbout24() {
        val a = eph.ayanamsha(j2000, ZodiacMode.Sidereal(Ayanamsha.LAHIRI))
        assertTrue(abs(a - 23.85) < 0.4, "Аянамша Лахири@J2000 = $a")
    }

    @Test
    fun nodeIsRetrograde() {
        val node = eph.position(j2000, CelestialBody.NORTH_NODE, ZodiacMode.Tropical)
        assertTrue(node.speedLongitude < 0.0, "Узел должен быть ретроградным, скорость=${node.speedLongitude}")
    }

    @Test
    fun allClassicalBodiesHaveFiniteLongitude() {
        for (b in CelestialBody.classical) {
            val l = lon(j2000, b)
            assertTrue(l in 0.0..360.0 && l == l, "Долгота $b вне диапазона: $l")
        }
        assertNotNull(eph)
    }
}
