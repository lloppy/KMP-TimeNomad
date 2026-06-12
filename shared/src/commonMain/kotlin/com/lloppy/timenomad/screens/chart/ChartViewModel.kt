package com.lloppy.timenomad.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lloppy.timenomad.astro.chart.ChartCalculator
import com.lloppy.timenomad.astro.model.Chart
import com.lloppy.timenomad.astro.time.AstroTime
import com.lloppy.timenomad.data.settings.AstroSettingsRepository
import com.lloppy.timenomad.data.profile.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ChartScreenState(
    val title: String = "",
    val subtitle: String = "",
    val chart: Chart? = null,
)

class ChartViewModel(
    private val profileId: String?,
    private val chartCalculator: ChartCalculator,
    private val astroSettings: AstroSettingsRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChartScreenState())
    val state: StateFlow<ChartScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(astroSettings.state, profileRepository.profiles) { _, _ -> }.collect { build() }
        }
    }

    private fun build() {
        val s = astroSettings.state.value
        val profile = profileId?.let { profileRepository.get(it) }
        if (profile != null) {
            val chart = chartCalculator.calculate(
                epochUtcMillis = profile.epochUtcMillis,
                latitude = profile.latitude,
                longitudeEast = profile.longitude,
                zodiacMode = s.zodiacMode,
                houseSystem = s.houseSystem,
            )
            _state.value = ChartScreenState(profile.name, profile.birthDateLabel + " • " + profile.placeLabel, chart)
        } else {
            val chart = chartCalculator.calculate(
                epochUtcMillis = AstroTime.nowUtcMillis(),
                latitude = s.homeLatitude,
                longitudeEast = s.homeLongitude,
                zodiacMode = s.zodiacMode,
                houseSystem = s.houseSystem,
            )
            _state.value = ChartScreenState("Карта неба", s.zodiacMode.label + " • " + s.homeLabel, chart)
        }
    }
}
