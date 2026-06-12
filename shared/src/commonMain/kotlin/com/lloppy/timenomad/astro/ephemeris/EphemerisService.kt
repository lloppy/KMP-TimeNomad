package com.lloppy.timenomad.astro.ephemeris

import com.lloppy.timenomad.astro.model.CelestialBody
import com.lloppy.timenomad.astro.model.PlanetPosition
import com.lloppy.timenomad.astro.model.ZodiacMode

interface EphemerisService {

    fun position(julianDayUt: Double, body: CelestialBody, mode: ZodiacMode): PlanetPosition

    fun positions(
        julianDayUt: Double,
        bodies: List<CelestialBody>,
        mode: ZodiacMode,
    ): List<PlanetPosition> = bodies.map { position(julianDayUt, it, mode) }

    fun ayanamsha(julianDayUt: Double, mode: ZodiacMode): Double
}
