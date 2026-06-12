package com.lloppy.timenomad.astro.aspects

import com.lloppy.timenomad.astro.math.AstroMath
import com.lloppy.timenomad.astro.model.AspectHit
import com.lloppy.timenomad.astro.model.AspectType
import com.lloppy.timenomad.astro.model.PlanetPosition
import kotlin.math.abs

class AspectCalculator(
    private val orbScale: Double = 1.0,
    private val includeMinor: Boolean = false,
) {
    fun find(positions: List<PlanetPosition>): List<AspectHit> {
        val types = if (includeMinor) AspectType.entries else AspectType.majors
        val result = mutableListOf<AspectHit>()
        for (i in positions.indices) {
            for (j in i + 1 until positions.size) {
                val a = positions[i]
                val b = positions[j]
                val separation = abs(AstroMath.norm180(a.longitude - b.longitude))
                var best: AspectHit? = null
                for (type in types) {
                    val orb = abs(separation - type.angle)
                    val allowed = type.defaultOrb * orbScale
                    val current = best
                    if (orb <= allowed && (current == null || orb < current.orb)) {
                        best = AspectHit(
                            first = a.body,
                            second = b.body,
                            type = type,
                            orb = orb,
                            applying = isApplying(a, b, type),
                        )
                    }
                }
                best?.let(result::add)
            }
        }
        return result.sortedBy { it.orb }
    }

    private fun isApplying(a: PlanetPosition, b: PlanetPosition, type: AspectType): Boolean {
        val sepNow = abs(AstroMath.norm180(a.longitude - b.longitude))
        val sepNext = abs(
            AstroMath.norm180(
                (a.longitude + a.speedLongitude) - (b.longitude + b.speedLongitude),
            ),
        )
        return abs(sepNext - type.angle) < abs(sepNow - type.angle)
    }
}
