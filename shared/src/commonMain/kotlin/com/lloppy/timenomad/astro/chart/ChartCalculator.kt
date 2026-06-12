package com.lloppy.timenomad.astro.chart

import com.lloppy.timenomad.astro.aspects.AspectCalculator
import com.lloppy.timenomad.astro.ephemeris.EphemerisService
import com.lloppy.timenomad.astro.houses.HouseCalculator
import com.lloppy.timenomad.astro.model.CelestialBody
import com.lloppy.timenomad.astro.model.Chart
import com.lloppy.timenomad.astro.model.ChartKind
import com.lloppy.timenomad.astro.model.HouseSystem
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.lloppy.timenomad.astro.time.AstroTime

class ChartCalculator(
    private val ephemeris: EphemerisService,
    private val aspectCalculator: AspectCalculator = AspectCalculator(),
) {
    fun calculate(
        epochUtcMillis: Long,
        latitude: Double,
        longitudeEast: Double,
        zodiacMode: ZodiacMode = ZodiacMode.Tropical,
        houseSystem: HouseSystem = HouseSystem.PLACIDUS,
        bodies: List<CelestialBody> = defaultBodies,
        kind: ChartKind = ChartKind.NATAL,
    ): Chart {
        val jd = AstroTime.julianDay(epochUtcMillis)
        val positions = ephemeris.positions(jd, bodies, zodiacMode)
        val houses = HouseCalculator.calculate(jd, latitude, longitudeEast, houseSystem)
        val ayan = ephemeris.ayanamsha(jd, zodiacMode)
        val aspects = aspectCalculator.find(positions)

        return Chart(
            kind = kind,
            momentUtcMillis = epochUtcMillis,
            julianDayUt = jd,
            latitude = latitude,
            longitude = longitudeEast,
            zodiacMode = zodiacMode,
            houseSystem = houseSystem,
            positions = positions,
            houses = houses.cusps.map { it.copy(cusp = norm(it.cusp - ayan)) },
            ascendant = norm(houses.ascendant - ayan),
            midheaven = norm(houses.midheaven - ayan),
            aspects = aspects,
        )
    }

    private fun norm(deg: Double): Double = ((deg % 360.0) + 360.0) % 360.0

    companion object {
        val defaultBodies: List<CelestialBody> = CelestialBody.classical + CelestialBody.NORTH_NODE
    }
}
