package com.lloppy.timenomad.theme

import androidx.compose.ui.graphics.Color
import com.lloppy.timenomad.astro.model.AspectType
import com.lloppy.timenomad.astro.model.CelestialBody
import com.lloppy.timenomad.astro.model.Element

object AstroColors {

    fun element(element: Element): Color = when (element) {
        Element.FIRE -> Color(0xFFE2674A)
        Element.EARTH -> Color(0xFF8FA45A)
        Element.AIR -> Color(0xFFE8C36B)
        Element.WATER -> Color(0xFF4FA3D1)
    }

    fun aspect(type: AspectType): Color = when (type) {
        AspectType.CONJUNCTION -> Color(0xFFE8C36B)
        AspectType.TRINE, AspectType.SEXTILE -> Color(0xFF3FC9C0)
        AspectType.SQUARE, AspectType.OPPOSITION -> Color(0xFFE2674A)
        else -> Color(0xFF8C8AA6)
    }

    fun planet(body: CelestialBody): Color = when (body) {
        CelestialBody.SUN -> Color(0xFFE8C36B)
        CelestialBody.MOON -> Color(0xFFCBD3E0)
        CelestialBody.MERCURY -> Color(0xFF8FD17A)
        CelestialBody.VENUS -> Color(0xFFE89BC6)
        CelestialBody.MARS -> Color(0xFFE2674A)
        CelestialBody.JUPITER -> Color(0xFFD9A24A)
        CelestialBody.SATURN -> Color(0xFF9D9CB0)
        CelestialBody.URANUS -> Color(0xFF5FC9D6)
        CelestialBody.NEPTUNE -> Color(0xFF6E8FE0)
        CelestialBody.PLUTO -> Color(0xFFB07A9E)
        CelestialBody.NORTH_NODE -> Color(0xFFB9B3E0)
    }
}
