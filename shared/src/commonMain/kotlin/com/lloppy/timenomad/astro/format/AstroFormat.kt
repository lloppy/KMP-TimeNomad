package com.lloppy.timenomad.astro.format

import com.lloppy.timenomad.astro.model.PlanetPosition
import com.lloppy.timenomad.astro.model.ZodiacSign
import kotlin.math.roundToInt

object AstroFormat {

    fun longitude(deg: Double): String {
        val norm = ((deg % 360.0) + 360.0) % 360.0
        val sign = ZodiacSign.fromLongitude(norm)
        val inSign = norm % 30.0
        var d = inSign.toInt()
        var m = ((inSign - d) * 60.0).roundToInt()
        if (m == 60) { m = 0; d += 1 }
        return "${d}°${pad(m)}′ ${sign.glyph}"
    }

    fun position(pos: PlanetPosition): String =
        longitude(pos.longitude) + if (pos.retrograde) " ℞" else ""

    fun signName(deg: Double): String = ZodiacSign.fromLongitude(deg).displayName

    private fun pad(v: Int): String = if (v < 10) "0$v" else v.toString()
}
