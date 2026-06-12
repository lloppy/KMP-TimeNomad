package com.lloppy.timenomad.astro.model

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
    NORTH_NODE("Сев. узел (Раху)", "☊", "Rahu"),
    SOUTH_NODE("Юж. узел (Кету)", "☋", "Ketu");

    val isLuminary: Boolean get() = this == SUN || this == MOON

    val isNode: Boolean get() = this == NORTH_NODE || this == SOUTH_NODE

    val canRetrograde: Boolean
        get() = this != SUN && this != MOON && !isNode

    companion object {
        val classical: List<CelestialBody> =
            listOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN, URANUS, NEPTUNE, PLUTO)

        val traditional: List<CelestialBody> =
            listOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)
    }
}
