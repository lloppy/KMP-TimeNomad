package com.lloppy.timenomad.astro.ephemeris

import com.lloppy.timenomad.astro.math.AstroMath.DEG2RAD
import com.lloppy.timenomad.astro.math.AstroMath.norm360
import kotlin.math.pow
import kotlin.math.sin

internal data class MoonPosition(val longitude: Double, val latitude: Double, val distanceKm: Double)

internal object MoonTheory {

    fun compute(t: Double): MoonPosition {
        val lp = norm360(218.3164477 + 481267.88123421 * t - 0.0015786 * t * t +
            t * t * t / 538841.0 - t * t * t * t / 65194000.0)
        val d = norm360(297.8501921 + 445267.1114034 * t - 0.0018819 * t * t +
            t * t * t / 545868.0 - t * t * t * t / 113065000.0)
        val m = norm360(357.5291092 + 35999.0502909 * t - 0.0001536 * t * t +
            t * t * t / 24490000.0)
        val mp = norm360(134.9633964 + 477198.8675055 * t + 0.0087414 * t * t +
            t * t * t / 69699.0 - t * t * t * t / 14712000.0)
        val f = norm360(93.2720950 + 483202.0175233 * t - 0.0036539 * t * t -
            t * t * t / 3526000.0 + t * t * t * t / 863310000.0)

        val a1 = norm360(119.75 + 131.849 * t)
        val a2 = norm360(53.09 + 479264.290 * t)
        val a3 = norm360(313.45 + 481266.484 * t)
        val e = 1.0 - 0.002516 * t - 0.0000074 * t * t

        var sumL = 0.0
        var sumR = 0.0
        for (row in LON) {
            val arg = row[0] * d + row[1] * m + row[2] * mp + row[3] * f
            val eFac = e.pow(kotlin.math.abs(row[1]))
            sumL += row[4] * eFac * sin(arg * DEG2RAD)
            sumR += row[5] * eFac * kotlin.math.cos(arg * DEG2RAD)
        }
        var sumB = 0.0
        for (row in LAT) {
            val arg = row[0] * d + row[1] * m + row[2] * mp + row[3] * f
            val eFac = e.pow(kotlin.math.abs(row[1]))
            sumB += row[4] * eFac * sin(arg * DEG2RAD)
        }

        sumL += 3958.0 * sin(a1 * DEG2RAD) + 1962.0 * sin((lp - f) * DEG2RAD) + 318.0 * sin(a2 * DEG2RAD)
        sumB += -2235.0 * sin(lp * DEG2RAD) + 382.0 * sin(a3 * DEG2RAD) +
            175.0 * sin((a1 - f) * DEG2RAD) + 175.0 * sin((a1 + f) * DEG2RAD) +
            127.0 * sin((lp - mp) * DEG2RAD) - 115.0 * sin((lp + mp) * DEG2RAD)

        val longitude = norm360(lp + sumL / 1_000_000.0)
        val latitude = sumB / 1_000_000.0
        val distance = 385000.56 + sumR / 1000.0
        return MoonPosition(longitude, latitude, distance)
    }

    private val LON: Array<DoubleArray> = arrayOf(
        doubleArrayOf(0.0, 0.0, 1.0, 0.0, 6288774.0, -20905355.0),
        doubleArrayOf(2.0, 0.0, -1.0, 0.0, 1274027.0, -3699111.0),
        doubleArrayOf(2.0, 0.0, 0.0, 0.0, 658314.0, -2955968.0),
        doubleArrayOf(0.0, 0.0, 2.0, 0.0, 213618.0, -569925.0),
        doubleArrayOf(0.0, 1.0, 0.0, 0.0, -185116.0, 48888.0),
        doubleArrayOf(0.0, 0.0, 0.0, 2.0, -114332.0, -3149.0),
        doubleArrayOf(2.0, 0.0, -2.0, 0.0, 58793.0, 246158.0),
        doubleArrayOf(2.0, -1.0, -1.0, 0.0, 57066.0, -152138.0),
        doubleArrayOf(2.0, 0.0, 1.0, 0.0, 53322.0, -170733.0),
        doubleArrayOf(2.0, -1.0, 0.0, 0.0, 45758.0, -204586.0),
        doubleArrayOf(0.0, 1.0, -1.0, 0.0, -40923.0, -129620.0),
        doubleArrayOf(1.0, 0.0, 0.0, 0.0, -34720.0, 108743.0),
        doubleArrayOf(0.0, 1.0, 1.0, 0.0, -30383.0, 104755.0),
        doubleArrayOf(2.0, 0.0, 0.0, -2.0, 15327.0, 10321.0),
        doubleArrayOf(0.0, 0.0, 1.0, 2.0, -12528.0, 0.0),
        doubleArrayOf(0.0, 0.0, 1.0, -2.0, 10980.0, 79661.0),
        doubleArrayOf(4.0, 0.0, -1.0, 0.0, 10675.0, -34782.0),
        doubleArrayOf(0.0, 0.0, 3.0, 0.0, 10034.0, -23210.0),
        doubleArrayOf(4.0, 0.0, -2.0, 0.0, 8548.0, -21636.0),
        doubleArrayOf(2.0, 1.0, -1.0, 0.0, -7888.0, 24208.0),
        doubleArrayOf(2.0, 1.0, 0.0, 0.0, -6766.0, 30824.0),
        doubleArrayOf(1.0, 0.0, -1.0, 0.0, -5163.0, -8379.0),
        doubleArrayOf(1.0, 1.0, 0.0, 0.0, 4987.0, -16675.0),
        doubleArrayOf(2.0, -1.0, 1.0, 0.0, 4036.0, -12831.0),
        doubleArrayOf(2.0, 0.0, 2.0, 0.0, 3994.0, -10445.0),
        doubleArrayOf(4.0, 0.0, 0.0, 0.0, 3861.0, -11650.0),
        doubleArrayOf(2.0, 0.0, -3.0, 0.0, 3665.0, 14403.0),
        doubleArrayOf(0.0, 1.0, -2.0, 0.0, -2689.0, -7003.0),
        doubleArrayOf(2.0, 0.0, -1.0, 2.0, -2602.0, 0.0),
        doubleArrayOf(2.0, -1.0, -2.0, 0.0, 2390.0, 10056.0),
        doubleArrayOf(1.0, 0.0, 1.0, 0.0, -2348.0, 6322.0),
        doubleArrayOf(2.0, -2.0, 0.0, 0.0, 2236.0, -9884.0),
        doubleArrayOf(0.0, 1.0, 2.0, 0.0, -2120.0, 5751.0),
        doubleArrayOf(0.0, 2.0, 0.0, 0.0, -2069.0, 0.0),
        doubleArrayOf(2.0, -2.0, -1.0, 0.0, 2048.0, -4950.0),
        doubleArrayOf(2.0, 0.0, 1.0, -2.0, -1773.0, 4130.0),
        doubleArrayOf(2.0, 0.0, 0.0, 2.0, -1595.0, 0.0),
        doubleArrayOf(4.0, -1.0, -1.0, 0.0, 1215.0, -3958.0),
        doubleArrayOf(0.0, 0.0, 2.0, 2.0, -1110.0, 0.0),
        doubleArrayOf(3.0, 0.0, -1.0, 0.0, -892.0, 3258.0),
        doubleArrayOf(2.0, 1.0, 1.0, 0.0, -810.0, 2616.0),
        doubleArrayOf(4.0, -1.0, -2.0, 0.0, 759.0, -1897.0),
        doubleArrayOf(0.0, 2.0, -1.0, 0.0, -713.0, -2117.0),
        doubleArrayOf(2.0, 2.0, -1.0, 0.0, -700.0, 2354.0),
        doubleArrayOf(2.0, 1.0, -2.0, 0.0, 691.0, 0.0),
        doubleArrayOf(2.0, -1.0, 0.0, -2.0, 596.0, 0.0),
        doubleArrayOf(4.0, 0.0, 1.0, 0.0, 549.0, -1423.0),
        doubleArrayOf(0.0, 0.0, 4.0, 0.0, 537.0, -1117.0),
        doubleArrayOf(4.0, -1.0, 0.0, 0.0, 520.0, -1571.0),
        doubleArrayOf(1.0, 0.0, -2.0, 0.0, -487.0, -1739.0),
        doubleArrayOf(2.0, 1.0, 0.0, -2.0, -399.0, 0.0),
        doubleArrayOf(0.0, 0.0, 2.0, -2.0, -381.0, -4421.0),
        doubleArrayOf(1.0, 1.0, 1.0, 0.0, 351.0, 0.0),
        doubleArrayOf(3.0, 0.0, -2.0, 0.0, -340.0, 0.0),
        doubleArrayOf(4.0, 0.0, -3.0, 0.0, 330.0, 0.0),
        doubleArrayOf(2.0, -1.0, 2.0, 0.0, 327.0, 0.0),
        doubleArrayOf(0.0, 2.0, 1.0, 0.0, -323.0, 1165.0),
        doubleArrayOf(1.0, 1.0, -1.0, 0.0, 299.0, 0.0),
        doubleArrayOf(2.0, 0.0, 3.0, 0.0, 294.0, 0.0),
        doubleArrayOf(2.0, 0.0, -1.0, -2.0, 0.0, 8752.0),
    )

    private val LAT: Array<DoubleArray> = arrayOf(
        doubleArrayOf(0.0, 0.0, 0.0, 1.0, 5128122.0),
        doubleArrayOf(0.0, 0.0, 1.0, 1.0, 280602.0),
        doubleArrayOf(0.0, 0.0, 1.0, -1.0, 277693.0),
        doubleArrayOf(2.0, 0.0, 0.0, -1.0, 173237.0),
        doubleArrayOf(2.0, 0.0, -1.0, 1.0, 55413.0),
        doubleArrayOf(2.0, 0.0, -1.0, -1.0, 46271.0),
        doubleArrayOf(2.0, 0.0, 0.0, 1.0, 32573.0),
        doubleArrayOf(0.0, 0.0, 2.0, 1.0, 17198.0),
        doubleArrayOf(2.0, 0.0, 1.0, -1.0, 9266.0),
        doubleArrayOf(0.0, 0.0, 2.0, -1.0, 8822.0),
        doubleArrayOf(2.0, -1.0, 0.0, -1.0, 8216.0),
        doubleArrayOf(2.0, 0.0, -2.0, -1.0, 4324.0),
        doubleArrayOf(2.0, 0.0, 1.0, 1.0, 4200.0),
        doubleArrayOf(2.0, 1.0, 0.0, -1.0, -3359.0),
        doubleArrayOf(2.0, -1.0, -1.0, 1.0, 2463.0),
        doubleArrayOf(2.0, -1.0, 0.0, 1.0, 2211.0),
        doubleArrayOf(2.0, -1.0, -1.0, -1.0, 2065.0),
        doubleArrayOf(0.0, 1.0, -1.0, -1.0, -1870.0),
        doubleArrayOf(4.0, 0.0, -1.0, -1.0, 1828.0),
        doubleArrayOf(0.0, 1.0, 0.0, 1.0, -1794.0),
        doubleArrayOf(0.0, 0.0, 0.0, 3.0, -1749.0),
        doubleArrayOf(0.0, 1.0, -1.0, 1.0, -1565.0),
        doubleArrayOf(1.0, 0.0, 0.0, 1.0, -1491.0),
        doubleArrayOf(0.0, 1.0, 1.0, 1.0, -1475.0),
        doubleArrayOf(0.0, 1.0, 1.0, -1.0, -1410.0),
        doubleArrayOf(0.0, 1.0, 0.0, -1.0, -1344.0),
        doubleArrayOf(1.0, 0.0, 0.0, -1.0, -1335.0),
        doubleArrayOf(0.0, 0.0, 3.0, 1.0, 1107.0),
        doubleArrayOf(4.0, 0.0, 0.0, -1.0, 1021.0),
        doubleArrayOf(4.0, 0.0, -1.0, 1.0, 833.0),
        doubleArrayOf(0.0, 0.0, 1.0, -3.0, 777.0),
        doubleArrayOf(4.0, 0.0, -2.0, 1.0, 671.0),
        doubleArrayOf(2.0, 0.0, 0.0, -3.0, 607.0),
        doubleArrayOf(2.0, 0.0, 2.0, -1.0, 596.0),
        doubleArrayOf(2.0, -1.0, 1.0, -1.0, 491.0),
        doubleArrayOf(2.0, 0.0, -2.0, 1.0, -451.0),
        doubleArrayOf(0.0, 0.0, 3.0, -1.0, 439.0),
        doubleArrayOf(2.0, 0.0, 2.0, 1.0, 422.0),
        doubleArrayOf(2.0, 0.0, -3.0, -1.0, 421.0),
        doubleArrayOf(2.0, 1.0, -1.0, 1.0, -366.0),
        doubleArrayOf(2.0, 1.0, 0.0, 1.0, -351.0),
        doubleArrayOf(4.0, 0.0, 0.0, 1.0, 331.0),
        doubleArrayOf(2.0, -1.0, 1.0, 1.0, 315.0),
        doubleArrayOf(2.0, -2.0, 0.0, -1.0, 302.0),
        doubleArrayOf(0.0, 0.0, 1.0, 3.0, -283.0),
        doubleArrayOf(2.0, 1.0, 1.0, -1.0, -229.0),
        doubleArrayOf(1.0, 1.0, 0.0, -1.0, 223.0),
        doubleArrayOf(1.0, 1.0, 0.0, 1.0, 223.0),
        doubleArrayOf(0.0, 1.0, -2.0, -1.0, -220.0),
        doubleArrayOf(2.0, 1.0, -1.0, -1.0, -220.0),
        doubleArrayOf(1.0, 0.0, 1.0, 1.0, -185.0),
        doubleArrayOf(2.0, -1.0, -2.0, -1.0, 181.0),
        doubleArrayOf(0.0, 1.0, 2.0, 1.0, -177.0),
        doubleArrayOf(4.0, 0.0, -2.0, -1.0, 176.0),
        doubleArrayOf(4.0, -1.0, -1.0, -1.0, 166.0),
        doubleArrayOf(1.0, 0.0, 1.0, -1.0, -164.0),
        doubleArrayOf(4.0, 0.0, 1.0, -1.0, 132.0),
        doubleArrayOf(1.0, 0.0, -1.0, -1.0, -119.0),
        doubleArrayOf(4.0, -1.0, 0.0, -1.0, 115.0),
        doubleArrayOf(2.0, -2.0, 0.0, 1.0, 107.0),
    )
}
