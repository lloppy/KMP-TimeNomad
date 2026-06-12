package com.lloppy.timenomad.astro.houses

import com.lloppy.timenomad.astro.math.AstroMath
import com.lloppy.timenomad.astro.math.AstroMath.asinD
import com.lloppy.timenomad.astro.math.AstroMath.atan2D
import com.lloppy.timenomad.astro.math.AstroMath.cosD
import com.lloppy.timenomad.astro.math.AstroMath.norm360
import com.lloppy.timenomad.astro.math.AstroMath.sinD
import com.lloppy.timenomad.astro.math.AstroMath.tanD
import com.lloppy.timenomad.astro.model.House
import com.lloppy.timenomad.astro.model.HouseSystem
import com.lloppy.timenomad.astro.time.AstroTime
import kotlin.math.abs

data class HouseResult(
    val cusps: List<House>,
    val ascendant: Double,
    val midheaven: Double,
)

object HouseCalculator {

    fun calculate(
        julianDayUt: Double,
        latitude: Double,
        longitudeEast: Double,
        system: HouseSystem,
    ): HouseResult {
        val ramc = AstroTime.localSiderealTime(julianDayUt, longitudeEast)
        val eps = AstroMath.meanObliquity(AstroTime.julianCenturiesT(julianDayUt))
        val mc = midheaven(ramc, eps)
        val asc = ascendant(ramc, eps, latitude)

        val cusps = when (system) {
            HouseSystem.WHOLE_SIGN -> wholeSign(asc)
            HouseSystem.EQUAL -> equal(asc)
            HouseSystem.PORPHYRY -> porphyry(asc, mc)
            HouseSystem.PLACIDUS, HouseSystem.KOCH ->
                placidus(ramc, eps, latitude, asc, mc) ?: porphyry(asc, mc)
        }
        return HouseResult(
            cusps = cusps.mapIndexed { i, c -> House(i + 1, c) },
            ascendant = asc,
            midheaven = mc,
        )
    }

    fun midheaven(ramc: Double, eps: Double): Double =
        norm360(atan2D(sinD(ramc), cosD(ramc) * cosD(eps)))

    fun ascendant(ramc: Double, eps: Double, latitude: Double): Double {
        val y = cosD(ramc)
        val x = -(sinD(ramc) * cosD(eps) + tanD(latitude) * sinD(eps))
        return norm360(atan2D(y, x))
    }

    private fun wholeSign(asc: Double): List<Double> {
        val start = (asc / 30.0).toInt() * 30.0
        return (0 until 12).map { norm360(start + it * 30.0) }
    }

    private fun equal(asc: Double): List<Double> =
        (0 until 12).map { norm360(asc + it * 30.0) }

    private fun porphyry(asc: Double, mc: Double): List<Double> {
        val ic = norm360(mc + 180.0)
        val desc = norm360(asc + 180.0)
        val cusps = DoubleArray(12)
        cusps[0] = asc; cusps[3] = ic; cusps[6] = desc; cusps[9] = mc
        fun fill(from: Int, startCusp: Double, endCusp: Double) {
            val arc = norm360(endCusp - startCusp)
            cusps[from] = norm360(startCusp + arc / 3.0)
            cusps[from + 1] = norm360(startCusp + 2.0 * arc / 3.0)
        }
        fill(1, asc, ic)
        fill(4, ic, desc)
        fill(7, desc, mc)
        fill(10, mc, asc)
        return cusps.toList()
    }

    private fun placidus(
        ramc: Double,
        eps: Double,
        latitude: Double,
        asc: Double,
        mc: Double,
    ): List<Double>? {
        if (abs(latitude) > 66.0) return null
        val c11 = semiArcCusp(ramc, eps, latitude, frac = 1.0 / 3.0, fromIc = false) ?: return null
        val c12 = semiArcCusp(ramc, eps, latitude, frac = 2.0 / 3.0, fromIc = false) ?: return null
        val c2 = semiArcCusp(ramc, eps, latitude, frac = 2.0 / 3.0, fromIc = true) ?: return null
        val c3 = semiArcCusp(ramc, eps, latitude, frac = 1.0 / 3.0, fromIc = true) ?: return null

        val cusps = DoubleArray(12)
        cusps[0] = asc
        cusps[9] = mc
        cusps[3] = norm360(mc + 180.0)
        cusps[6] = norm360(asc + 180.0)
        cusps[10] = c11
        cusps[11] = c12
        cusps[1] = c2
        cusps[2] = c3
        cusps[4] = norm360(c11 + 180.0)
        cusps[5] = norm360(c12 + 180.0)
        cusps[7] = norm360(c2 + 180.0)
        cusps[8] = norm360(c3 + 180.0)
        return cusps.toList()
    }

    private fun semiArcCusp(
        ramc: Double,
        eps: Double,
        latitude: Double,
        frac: Double,
        fromIc: Boolean,
    ): Double? {
        var ra = if (fromIc) ramc + 180.0 else ramc + frac * 90.0
        repeat(30) {
            val lambda = norm360(atan2D(sinD(ra), cosD(ra) * cosD(eps)))
            val decl = asinD(sinD(eps) * sinD(lambda))
            val tanProduct = tanD(latitude) * tanD(decl)
            if (abs(tanProduct) >= 1.0) return null
            val ad = asinD(tanProduct)
            val next = if (fromIc) {
                ramc + 180.0 - frac * (90.0 - ad)
            } else {
                ramc + frac * (90.0 + ad)
            }
            if (abs(AstroMath.norm180(next - ra)) < 1e-7) {
                return norm360(atan2D(sinD(next), cosD(next) * cosD(eps)))
            }
            ra = next
        }
        return norm360(atan2D(sinD(ra), cosD(ra) * cosD(eps)))
    }
}
