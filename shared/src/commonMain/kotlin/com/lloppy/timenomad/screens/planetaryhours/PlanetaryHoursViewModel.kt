package com.lloppy.timenomad.screens.planetaryhours

import androidx.lifecycle.ViewModel
import com.lloppy.timenomad.astro.planetaryhours.PlanetaryHoursCalculator
import com.lloppy.timenomad.astro.planetaryhours.PlanetaryHoursResult
import com.lloppy.timenomad.astro.time.AstroTime
import com.lloppy.timenomad.data.settings.AstroSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlanetaryHoursState(
    val result: PlanetaryHoursResult? = null,
    val locationLabel: String = "",
    val unavailable: Boolean = false,
)

class PlanetaryHoursViewModel(
    private val calculator: PlanetaryHoursCalculator,
    private val astroSettings: AstroSettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PlanetaryHoursState())
    val state: StateFlow<PlanetaryHoursState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        val s = astroSettings.state.value
        val result = calculator.calculate(AstroTime.nowUtcMillis(), s.homeLatitude, s.homeLongitude)
        _state.value = PlanetaryHoursState(
            result = result,
            locationLabel = s.homeLabel,
            unavailable = result == null,
        )
    }
}
