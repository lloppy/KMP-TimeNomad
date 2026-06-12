package com.lloppy.timenomad.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lloppy.timenomad.astro.model.Ayanamsha
import com.lloppy.timenomad.astro.model.HouseSystem
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.lloppy.timenomad.data.geo.GeocodingService
import com.lloppy.timenomad.data.geo.PlaceSearchState
import com.lloppy.timenomad.data.settings.AstroSettings
import com.lloppy.timenomad.data.settings.AstroSettingsRepository
import com.lloppy.timenomad.settings.SettingsRepository
import com.lloppy.timenomad.settings.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsScreenState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val astro: AstroSettings = AstroSettings(),
)

class SettingsViewModel(
    private val themeSettings: SettingsRepository,
    private val astroSettings: AstroSettingsRepository,
    private val geocoding: GeocodingService,
) : ViewModel() {

    val state: StateFlow<SettingsScreenState> =
        combine(themeSettings.themeMode, astroSettings.state) { theme, astro ->
            SettingsScreenState(theme, astro)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsScreenState())

    private val _search = MutableStateFlow(PlaceSearchState())
    val search: StateFlow<PlaceSearchState> = _search.asStateFlow()

    fun setTheme(mode: ThemeMode) = themeSettings.setThemeMode(mode)
    fun setTropical() = astroSettings.setTropical()
    fun setSidereal(ayanamsha: Ayanamsha) = astroSettings.setSidereal(ayanamsha)
    fun setHouseSystem(system: HouseSystem) = astroSettings.setHouseSystem(system)
    fun setHome(lat: Double, lon: Double, label: String) = astroSettings.setHomeLocation(lat, lon, label)

    fun searchPlace(query: String) {
        if (query.isBlank()) return
        _search.value = PlaceSearchState(loading = true)
        viewModelScope.launch {
            geocoding.search(query)
                .onSuccess { _search.value = PlaceSearchState(results = it) }
                .onFailure { _search.value = PlaceSearchState(error = it.message ?: "Ошибка поиска") }
        }
    }

    fun clearSearch() {
        _search.value = PlaceSearchState()
    }

    fun currentAyanamsha(): Ayanamsha =
        (state.value.astro.zodiacMode as? ZodiacMode.Sidereal)?.ayanamsha ?: Ayanamsha.LAHIRI
}
