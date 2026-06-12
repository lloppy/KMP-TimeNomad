package com.lloppy.timenomad.astro.model

enum class Element(val displayName: String) {
    FIRE("Огонь"), EARTH("Земля"), AIR("Воздух"), WATER("Вода")
}

enum class Modality(val displayName: String) {
    CARDINAL("Кардинальный"), FIXED("Фиксированный"), MUTABLE("Мутабельный")
}

/**
 * 12 знаков зодиака. [index] 0..11 соответствует эклиптической долготе [index*30, index*30+30).
 */
enum class ZodiacSign(
    val displayName: String,
    val glyph: String,
    val element: Element,
    val modality: Modality,
    val ruler: CelestialBody,
) {
    ARIES("Овен", "♈", Element.FIRE, Modality.CARDINAL, CelestialBody.MARS),
    TAURUS("Телец", "♉", Element.EARTH, Modality.FIXED, CelestialBody.VENUS),
    GEMINI("Близнецы", "♊", Element.AIR, Modality.MUTABLE, CelestialBody.MERCURY),
    CANCER("Рак", "♋", Element.WATER, Modality.CARDINAL, CelestialBody.MOON),
    LEO("Лев", "♌", Element.FIRE, Modality.FIXED, CelestialBody.SUN),
    VIRGO("Дева", "♍", Element.EARTH, Modality.MUTABLE, CelestialBody.MERCURY),
    LIBRA("Весы", "♎", Element.AIR, Modality.CARDINAL, CelestialBody.VENUS),
    SCORPIO("Скорпион", "♏", Element.WATER, Modality.FIXED, CelestialBody.MARS),
    SAGITTARIUS("Стрелец", "♐", Element.FIRE, Modality.MUTABLE, CelestialBody.JUPITER),
    CAPRICORN("Козерог", "♑", Element.EARTH, Modality.CARDINAL, CelestialBody.SATURN),
    AQUARIUS("Водолей", "♒", Element.AIR, Modality.FIXED, CelestialBody.SATURN),
    PISCES("Рыбы", "♓", Element.WATER, Modality.MUTABLE, CelestialBody.JUPITER);

    val index: Int get() = ordinal

    companion object {
        /** Знак по эклиптической долготе (любое значение нормализуется в 0..360). */
        fun fromLongitude(longitude: Double): ZodiacSign {
            val norm = ((longitude % 360.0) + 360.0) % 360.0
            return entries[(norm / 30.0).toInt().coerceIn(0, 11)]
        }
    }
}
