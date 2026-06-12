package com.lloppy.timenomad.astro.math

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

object AstroMath {
    const val DEG2RAD: Double = PI / 180.0
    const val RAD2DEG: Double = 180.0 / PI

    fun norm360(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0) d += 360.0
        return d
    }

    fun norm180(deg: Double): Double {
        var d = norm360(deg)
        if (d > 180.0) d -= 360.0
        return d
    }

    fun sinD(deg: Double): Double = sin(deg * DEG2RAD)
    fun cosD(deg: Double): Double = cos(deg * DEG2RAD)
    fun tanD(deg: Double): Double = tan(deg * DEG2RAD)
    fun asinD(x: Double): Double = asin(x.coerceIn(-1.0, 1.0)) * RAD2DEG

    fun acosDOrNull(x: Double): Double? = if (x < -1.0 || x > 1.0) null else acos(x) * RAD2DEG
    fun atan2D(y: Double, x: Double): Double = atan2(y, x) * RAD2DEG

    fun angularSeparation(a: Double, b: Double): Double = norm180(a - b)

    fun meanObliquity(t: Double): Double =
        23.439291111111 - 0.0130041667 * t - 1.638889e-7 * t * t + 5.036111e-7 * t * t * t
}
