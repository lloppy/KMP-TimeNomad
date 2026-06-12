package com.lloppy.timenomad.astro.chart

import com.lloppy.timenomad.astro.aspects.AspectCalculator
import com.lloppy.timenomad.astro.ephemeris.EphemerisService
import com.lloppy.timenomad.astro.model.Chart
import com.lloppy.timenomad.astro.model.PlanetPosition
import com.lloppy.timenomad.astro.model.TransitHit
import com.lloppy.timenomad.astro.time.AstroTime

data class TransitResult(
    val momentUtcMillis: Long,
    val positions: List<PlanetPosition>,
    val aspects: List<TransitHit>,
)

class TransitCalculator(
    private val ephemeris: EphemerisService,
    private val aspectCalculator: AspectCalculator = AspectCalculator(),
) {
    fun calculate(natal: Chart, momentUtcMillis: Long): TransitResult {
        val jd = AstroTime.julianDay(momentUtcMillis)
        val positions = ephemeris.positions(jd, ChartCalculator.defaultBodies, natal.zodiacMode)
        val aspects = aspectCalculator.findTransits(natal.positions, positions)
        return TransitResult(momentUtcMillis, positions, aspects)
    }
}
