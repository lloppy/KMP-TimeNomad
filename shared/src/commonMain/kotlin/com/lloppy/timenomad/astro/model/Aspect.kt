package com.lloppy.timenomad.astro.model

/**
 * Тип аспекта: точный угол между телами и дефолтный орбис.
 * [harmonious] = true для гармоничных (трин/секстиль), false для напряжённых.
 */
enum class AspectType(
    val displayName: String,
    val glyph: String,
    val angle: Double,
    val defaultOrb: Double,
    val major: Boolean,
    val harmonious: Boolean?,
) {
    CONJUNCTION("Соединение", "☌", 0.0, 8.0, true, null),
    SEXTILE("Секстиль", "⚹", 60.0, 6.0, true, true),
    SQUARE("Квадрат", "□", 90.0, 7.0, true, false),
    TRINE("Тригон", "△", 120.0, 8.0, true, true),
    OPPOSITION("Оппозиция", "☍", 180.0, 8.0, true, false),
    SEMISEXTILE("Полусекстиль", "⚺", 30.0, 2.0, false, true),
    SEMISQUARE("Полуквадрат", "∠", 45.0, 2.0, false, false),
    SESQUIQUADRATE("Полутораквадрат", "⚼", 135.0, 2.0, false, false),
    QUINCUNX("Квинконс", "⚻", 150.0, 3.0, false, false);

    companion object {
        val majors: List<AspectType> get() = entries.filter { it.major }
    }
}

/**
 * Найденный аспект между двумя телами карты.
 * [orb] — отклонение от точного угла (в градусах), [applying] — сходящийся ли аспект.
 */
data class AspectHit(
    val first: CelestialBody,
    val second: CelestialBody,
    val type: AspectType,
    val orb: Double,
    val applying: Boolean,
)
