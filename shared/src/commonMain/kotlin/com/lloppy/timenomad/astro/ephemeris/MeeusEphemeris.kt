package com.lloppy.timenomad.astro.ephemeris

import com.lloppy.timenomad.astro.math.AstroMath
import com.lloppy.timenomad.astro.math.AstroMath.atan2D
import com.lloppy.timenomad.astro.math.AstroMath.norm360
import com.lloppy.timenomad.astro.model.CelestialBody
import com.lloppy.timenomad.astro.model.PlanetPosition
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.lloppy.timenomad.astro.time.AstroTime
import kotlin.math.sqrt

class MeeusEphemeris : EphemerisService {

    override fun position(julianDayUt: Double, body: CelestialBody, mode: ZodiacMode): PlanetPosition {
        val ayan = ayanamsha(julianDayUt, mode)
        val now = geocentric(julianDayUt)
            .let { it.body(body) }
        val ahead = geocentric(julianDayUt + SPEED_DT).body(body).longitudeTropical
        val behind = geocentric(julianDayUt - SPEED_DT).body(body).longitudeTropical
        val speed = AstroMath.norm180(ahead - behind) / (2.0 * SPEED_DT)

        val longitude = norm360(now.longitudeTropical - ayan)
        return PlanetPosition(
            body = body,
            longitude = longitude,
            latitude = now.latitude,
            distanceAu = now.distanceAu,
            speedLongitude = speed,
            declination = now.declination,
        )
    }

    override fun ayanamsha(julianDayUt: Double, mode: ZodiacMode): Double = when (mode) {
        ZodiacMode.Tropical -> 0.0
        is ZodiacMode.Sidereal -> {
            val t = AstroTime.julianCenturiesT(julianDayUt)
            val base = when (mode.ayanamsha) {
                com.lloppy.timenomad.astro.model.Ayanamsha.LAHIRI -> 23.853
                com.lloppy.timenomad.astro.model.Ayanamsha.FAGAN_BRADLEY -> 24.736
            }
            base + PRECESSION_PER_CENTURY * t
        }
    }


    private class BodyState(
        val longitudeTropical: Double,
        val latitude: Double,
        val distanceAu: Double,
        val declination: Double,
    )

    private inner class Frame(val jd: Double) {
        val t = AstroTime.julianCenturiesT(jd)
        val precession = PRECESSION_PER_CENTURY * t
        val obliquity = AstroMath.meanObliquity(t)
        val earth = KeplerianBody.EARTH.heliocentric(t)

        fun body(b: CelestialBody): BodyState = when (b) {
            CelestialBody.SUN -> fromVector(EclipticVector(-earth.x, -earth.y, -earth.z))
            CelestialBody.MOON -> {
                val m = MoonTheory.compute(t)
                state(m.longitude, m.latitude, m.distanceKm / AU_KM)
            }
            CelestialBody.NORTH_NODE -> {
                val node = norm360(
                    125.0445479 - 1934.1362891 * t + 0.0020754 * t * t +
                        t * t * t / 467410.0 - t * t * t * t / 60616000.0,
                )
                state(node, 0.0, 0.0)
            }
            else -> {
                val planet = keplerianFor(b).heliocentric(t)
                fromVector(EclipticVector(planet.x - earth.x, planet.y - earth.y, planet.z - earth.z))
            }
        }

        private fun fromVector(v: EclipticVector): BodyState {
            val lonJ2000 = norm360(atan2D(v.y, v.x))
            val lat = atan2D(v.z, sqrt(v.x * v.x + v.y * v.y))
            val dist = sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
            return state(norm360(lonJ2000 + precession), lat, dist)
        }

        private fun state(lonOfDate: Double, lat: Double, distAu: Double): BodyState {
            val decl = declination(lonOfDate, lat, obliquity)
            return BodyState(lonOfDate, lat, distAu, decl)
        }
    }

    private fun geocentric(jd: Double): Frame = Frame(jd)

    private fun declination(lon: Double, lat: Double, obliquity: Double): Double {
        val sinDec = AstroMath.sinD(lat) * AstroMath.cosD(obliquity) +
            AstroMath.cosD(lat) * AstroMath.sinD(obliquity) * AstroMath.sinD(lon)
        return AstroMath.asinD(sinDec)
    }

    private fun keplerianFor(b: CelestialBody): KeplerianBody = when (b) {
        CelestialBody.MERCURY -> KeplerianBody.MERCURY
        CelestialBody.VENUS -> KeplerianBody.VENUS
        CelestialBody.MARS -> KeplerianBody.MARS
        CelestialBody.JUPITER -> KeplerianBody.JUPITER
        CelestialBody.SATURN -> KeplerianBody.SATURN
        CelestialBody.URANUS -> KeplerianBody.URANUS
        CelestialBody.NEPTUNE -> KeplerianBody.NEPTUNE
        CelestialBody.PLUTO -> KeplerianBody.PLUTO
        else -> error("Нет кеплеровских элементов для $b")
    }

    private companion object {
        const val AU_KM = 149_597_870.7
        const val SPEED_DT = 0.5
        const val PRECESSION_PER_CENTURY = 1.396971
    }
}
