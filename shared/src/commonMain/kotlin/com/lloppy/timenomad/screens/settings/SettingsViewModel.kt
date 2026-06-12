package com.lloppy.timenomad.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lloppy.timenomad.astro.model.Ayanamsha
import com.lloppy.timenomad.astro.model.HouseSystem
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.lloppy.timenomad.data.settings.AstroSettings
import com.lloppy.timenomad.data.settings.AstroSettingsRepository
import com.lloppy.timenomad.settings.SettingsRepository
import com.lloppy.timenomad.settings.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SettingsScreenState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val astro: AstroSettings = AstroSettings(),
)

class SettingsViewModel(
    private val themeSettings: SettingsRepository,
    private val astroSettings: AstroSettingsRepository,
) : ViewModel() {

    val state: StateFlow<SettingsScreenState> =
        combine(themeSettings.themeMode, astroSettings.state) { theme, astro ->
            SettingsScreenState(theme, astro)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsScreenState())

    fun setTheme(mode: ThemeMode) = themeSettings.setThemeMode(mode)
    fun setTropical() = astroSettings.setTropical()
    fun setSidereal(ayanamsha: Ayanamsha) = astroSettings.setSidereal(ayanamsha)
    fun setHouseSystem(system: HouseSystem) = astroSettings.setHouseSystem(system)
    fun setHome(lat: Double, lon: Double, label: String) = astroSettings.setHomeLocation(lat, lon, label)

    fun currentAyanamsha(): Ayanamsha =
        (state.value.astro.zodiacMode as? ZodiacMode.Sidereal)?.ayanamsha ?: Ayanamsha.LAHIRI
}
