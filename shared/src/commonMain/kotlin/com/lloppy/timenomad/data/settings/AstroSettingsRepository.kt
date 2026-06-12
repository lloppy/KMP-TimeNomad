package com.lloppy.timenomad.data.settings

import com.lloppy.timenomad.astro.model.Ayanamsha
import com.lloppy.timenomad.astro.model.HouseSystem
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Астрологические настройки расчётов: зодиак, система домов, аянамша, домашняя локация. */
data class AstroSettings(
    val zodiacMode: ZodiacMode = ZodiacMode.Tropical,
    val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
    val homeLatitude: Double = 51.4779,
    val homeLongitude: Double = 0.0,
    val homeLabel: String = "Гринвич",
)

class AstroSettingsRepository(private val settings: Settings) {

    private val _state = MutableStateFlow(load())
    val state: StateFlow<AstroSettings> = _state.asStateFlow()

    fun setTropical() = update(_state.value.copy(zodiacMode = ZodiacMode.Tropical))

    fun setSidereal(ayanamsha: Ayanamsha) =
        update(_state.value.copy(zodiacMode = ZodiacMode.Sidereal(ayanamsha)))

    fun setHouseSystem(system: HouseSystem) =
        update(_state.value.copy(houseSystem = system))

    fun setHomeLocation(latitude: Double, longitude: Double, label: String) =
        update(_state.value.copy(homeLatitude = latitude, homeLongitude = longitude, homeLabel = label))

    private fun update(value: AstroSettings) {
        _state.value = value
        persist(value)
    }

    private fun persist(value: AstroSettings) {
        settings.putString(KEY_HOUSE, value.houseSystem.name)
        settings.putDouble(KEY_HOME_LAT, value.homeLatitude)
        settings.putDouble(KEY_HOME_LON, value.homeLongitude)
        settings.putString(KEY_HOME_LABEL, value.homeLabel)
        when (val z = value.zodiacMode) {
            ZodiacMode.Tropical -> settings.putString(KEY_ZODIAC, ZODIAC_TROPICAL)
            is ZodiacMode.Sidereal -> {
                settings.putString(KEY_ZODIAC, ZODIAC_SIDEREAL)
                settings.putString(KEY_AYANAMSHA, z.ayanamsha.name)
            }
        }
    }

    private fun load(): AstroSettings {
        val house = settings.getStringOrNull(KEY_HOUSE)
            ?.let { runCatching { HouseSystem.valueOf(it) }.getOrNull() }
            ?: HouseSystem.PLACIDUS
        val zodiac = when (settings.getStringOrNull(KEY_ZODIAC)) {
            ZODIAC_SIDEREAL -> {
                val ayan = settings.getStringOrNull(KEY_AYANAMSHA)
                    ?.let { runCatching { Ayanamsha.valueOf(it) }.getOrNull() }
                    ?: Ayanamsha.LAHIRI
                ZodiacMode.Sidereal(ayan)
            }
            else -> ZodiacMode.Tropical
        }
        return AstroSettings(
            zodiacMode = zodiac,
            houseSystem = house,
            homeLatitude = settings.getDoubleOrNull(KEY_HOME_LAT) ?: 51.4779,
            homeLongitude = settings.getDoubleOrNull(KEY_HOME_LON) ?: 0.0,
            homeLabel = settings.getStringOrNull(KEY_HOME_LABEL) ?: "Гринвич",
        )
    }

    private companion object {
        const val KEY_ZODIAC = "astro.zodiac"
        const val KEY_AYANAMSHA = "astro.ayanamsha"
        const val KEY_HOUSE = "astro.house"
        const val KEY_HOME_LAT = "astro.home.lat"
        const val KEY_HOME_LON = "astro.home.lon"
        const val KEY_HOME_LABEL = "astro.home.label"
        const val ZODIAC_TROPICAL = "tropical"
        const val ZODIAC_SIDEREAL = "sidereal"
    }
}
