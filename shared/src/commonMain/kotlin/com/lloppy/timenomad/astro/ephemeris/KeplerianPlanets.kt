package com.lloppy.timenomad.astro.ephemeris

import com.lloppy.timenomad.astro.math.AstroMath.DEG2RAD
import com.lloppy.timenomad.astro.math.AstroMath.norm180
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal data class EclipticVector(val x: Double, val y: Double, val z: Double)

internal enum class KeplerianBody(
    val a0: Double, val aDot: Double,
    val e0: Double, val eDot: Double,
    val i0: Double, val iDot: Double,
    val l0: Double, val lDot: Double,
    val peri0: Double, val periDot: Double,
    val node0: Double, val nodeDot: Double,
    val b: Double = 0.0, val c: Double = 0.0, val s: Double = 0.0, val f: Double = 0.0,
) {
    MERCURY(
        0.38709843, 0.0, 0.20563661, 0.00002123, 7.00559432, -0.00590158,
        252.25166724, 149472.67486623, 77.45771895, 0.15940013, 48.33961819, -0.12214182,
    ),
    VENUS(
        0.72332102, -0.00000026, 0.00676399, -0.00005107, 3.39777545, 0.00043494,
        181.97970850, 58517.81560260, 131.76755713, 0.05679648, 76.67261496, -0.27274174,
    ),
    EARTH(
        1.00000018, -0.00000003, 0.01673163, -0.00003661, -0.00054346, -0.01337178,
        100.46691572, 35999.37306329, 102.93005885, 0.31795260, -5.11260389, -0.24123856,
    ),
    MARS(
        1.52371243, 0.00000097, 0.09336511, 0.00009149, 1.85181869, -0.00724757,
        -4.56813164, 19140.29934243, -23.91744784, 0.45223625, 49.71320984, -0.26852431,
    ),
    JUPITER(
        5.20248019, -0.00002864, 0.04853590, 0.00018026, 1.29861416, -0.00322699,
        34.33479152, 3034.90371757, 14.27495244, 0.18199196, 100.29282654, 0.13024619,
        b = -0.00012452, c = 0.06064060, s = -0.35635438, f = 38.35125000,
    ),
    SATURN(
        9.54149883, -0.00003065, 0.05550825, -0.00032044, 2.49424102, 0.00451969,
        50.07571329, 1222.11494724, 92.86136063, 0.54179478, 113.63998702, -0.25015002,
        b = 0.00025899, c = -0.13434469, s = 0.87320147, f = 38.35125000,
    ),
    URANUS(
        19.18797948, -0.00020455, 0.04685740, -0.00001550, 0.77298127, -0.00180155,
        314.20276625, 428.49512595, 172.43404441, 0.09266985, 73.96250215, 0.05739699,
        b = 0.00058331, c = -0.97731848, s = 0.17689245, f = 7.67025000,
    ),
    NEPTUNE(
        30.06952752, 0.00006447, 0.00895439, 0.00000818, 1.77005520, 0.00022400,
        304.22289287, 218.46515314, 46.68158724, 0.01009938, 131.78635853, -0.00606302,
        b = -0.00041348, c = 0.68346318, s = -0.10162547, f = 7.67025000,
    ),
    PLUTO(
        39.48686035, 0.00449751, 0.24885238, 0.00006016, 17.14104260, 0.00000501,
        238.96535011, 145.18042903, 224.09702598, -0.00968827, 110.30167986, -0.00809981,
        b = -0.01262724,
    );

    /** Гелиоцентрический вектор в эклиптике J2000 для юлианских веков [t] от J2000. */
    fun heliocentric(t: Double): EclipticVector {
        val a = a0 + aDot * t
        val e = e0 + eDot * t
        val i = i0 + iDot * t
        val l = l0 + lDot * t
        val peri = peri0 + periDot * t
        val node = node0 + nodeDot * t

        val argPeri = peri - node
        var m = l - peri + b * t * t + c * cos(f * t * DEG2RAD) + s * sin(f * t * DEG2RAD)
        m = norm180(m)

        val eAnom = solveKepler(m, e)
        val xv = a * (cos(eAnom * DEG2RAD) - e)
        val yv = a * sqrt(1.0 - e * e) * sin(eAnom * DEG2RAD)

        val w = argPeri * DEG2RAD
        val o = node * DEG2RAD
        val inc = i * DEG2RAD
        val cosW = cos(w); val sinW = sin(w)
        val cosO = cos(o); val sinO = sin(o)
        val cosI = cos(inc); val sinI = sin(inc)

        val x = (cosW * cosO - sinW * sinO * cosI) * xv + (-sinW * cosO - cosW * sinO * cosI) * yv
        val y = (cosW * sinO + sinW * cosO * cosI) * xv + (-sinW * sinO + cosW * cosO * cosI) * yv
        val z = (sinW * sinI) * xv + (cosW * sinI) * yv
        return EclipticVector(x, y, z)
    }

    private fun solveKepler(mDeg: Double, e: Double): Double {
        val m = mDeg * DEG2RAD
        var eAnom = m + e * sin(m)
        repeat(12) {
            val dm = m - (eAnom - e * sin(eAnom))
            val de = dm / (1.0 - e * cos(eAnom))
            eAnom += de
            if (abs(de) < 1e-9) return eAnom / DEG2RAD
        }
        return eAnom / DEG2RAD
    }
}
