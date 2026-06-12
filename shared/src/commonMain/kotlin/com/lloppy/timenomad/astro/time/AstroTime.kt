package com.lloppy.timenomad.astro.time

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Географическая точка. Долгота: восток положительный, запад отрицательный. */
data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val label: String = "",
) {
    companion object {
        /** Дефолт для расчёта текущего неба, если профиль/локация не заданы. */
        val GREENWICH = GeoLocation(51.4779, 0.0, "Гринвич")
    }
}

object AstroTime {

    const val J2000: Double = 2451545.0
    const val JD_UNIX_EPOCH: Double = 2440587.5

    @OptIn(ExperimentalTime::class)
    fun nowUtcMillis(): Long = Clock.System.now().toEpochMilliseconds()

    /** Юлианская дата (UT) из Unix-времени в миллисекундах. */
    fun julianDay(epochMillis: Long): Double = epochMillis / 86_400_000.0 + JD_UNIX_EPOCH

    fun epochMillis(julianDay: Double): Long =
        ((julianDay - JD_UNIX_EPOCH) * 86_400_000.0).toLong()

    /** Века по 36525 суток от эпохи J2000.0. */
    fun julianCenturiesT(julianDay: Double): Double = (julianDay - J2000) / 36525.0

    /**
     * Unix-миллисекунды из локальных даты/времени и смещения часового пояса (в минутах от UTC).
     * Перевод в UTC выполняется вычитанием смещения — без обращения к базе часовых поясов.
     */
    fun epochMillisFromLocal(
        local: LocalDateTime,
        utcOffsetMinutes: Int,
    ): Long {
        val days = local.date.toEpochDays().toLong()
        val secondsOfDay = local.time.toSecondOfDay().toLong()
        val utcSeconds = days * 86_400L + secondsOfDay - utcOffsetMinutes * 60L
        return utcSeconds * 1000L
    }

    fun localDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): LocalDateTime =
        LocalDateTime(LocalDate(year, month, day), LocalTime(hour, minute))

    /**
     * Среднее гринвичское звёздное время (GMST) в градусах для заданной JD (UT).
     * Формула Meeus (гл. 12).
     */
    fun greenwichMeanSiderealTime(julianDay: Double): Double {
        val t = julianCenturiesT(julianDay)
        var gmst = 280.46061837 +
            360.98564736629 * (julianDay - J2000) +
            0.000387933 * t * t -
            t * t * t / 38_710_000.0
        gmst %= 360.0
        if (gmst < 0) gmst += 360.0
        return gmst
    }

    /** Местное звёздное время (LST) в градусах. */
    fun localSiderealTime(julianDay: Double, longitudeEast: Double): Double {
        var lst = greenwichMeanSiderealTime(julianDay) + longitudeEast
        lst %= 360.0
        if (lst < 0) lst += 360.0
        return lst
    }
}
