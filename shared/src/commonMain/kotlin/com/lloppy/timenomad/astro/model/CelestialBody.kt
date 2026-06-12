package com.lloppy.timenomad.astro.model

/**
 * Небесные тела и расчётные точки, используемые в карте.
 * [glyph] — астрологический символ (Unicode), [shortName] — компактная подпись.
 */
enum class CelestialBody(val displayName: String, val glyph: String, val shortName: String) {
    SUN("Солнце", "☉", "Sun"),
    MOON("Луна", "☽", "Moon"),
    MERCURY("Меркурий", "☿", "Mer"),
    VENUS("Венера", "♀", "Ven"),
    MARS("Марс", "♂", "Mar"),
    JUPITER("Юпитер", "♃", "Jup"),
    SATURN("Сатурн", "♄", "Sat"),
    URANUS("Уран", "♅", "Ura"),
    NEPTUNE("Нептун", "♆", "Nep"),
    PLUTO("Плутон", "♇", "Plu"),
    NORTH_NODE("Сев. узел", "☊", "Node");

    val isLuminary: Boolean get() = this == SUN || this == MOON

    /** Планеты, которые могут быть ретроградными (узлы/светила обрабатываем отдельно). */
    val canRetrograde: Boolean
        get() = this != SUN && this != MOON && this != NORTH_NODE

    companion object {
        /** Классический набор для дашборда и базовой натальной карты. */
        val classical: List<CelestialBody> =
            listOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN, URANUS, NEPTUNE, PLUTO)

        /** Семь традиционных планет (для планетарных часов/дней). */
        val traditional: List<CelestialBody> =
            listOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)
    }
}
