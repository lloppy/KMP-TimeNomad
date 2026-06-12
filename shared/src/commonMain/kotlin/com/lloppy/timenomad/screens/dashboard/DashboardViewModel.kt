package com.lloppy.timenomad.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lloppy.timenomad.astro.chart.ChartCalculator
import com.lloppy.timenomad.astro.chart.MoonPhase
import com.lloppy.timenomad.astro.model.CelestialBody
import com.lloppy.timenomad.astro.model.Chart
import com.lloppy.timenomad.astro.model.PlanetPosition
import com.lloppy.timenomad.astro.time.AstroTime
import com.lloppy.timenomad.data.settings.AstroSettings
import com.lloppy.timenomad.data.settings.AstroSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DashboardState(
    val loading: Boolean = true,
    val chart: Chart? = null,
    val moonPhase: MoonPhase? = null,
    val retrogrades: List<PlanetPosition> = emptyList(),
    val momentLabel: String = "",
    val locationLabel: String = "",
    val zodiacLabel: String = "",
)

class DashboardViewModel(
    private val chartCalculator: ChartCalculator,
    private val astroSettings: AstroSettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            astroSettings.state.collect { rebuild(it) }
        }
    }

    fun refresh() = rebuild(astroSettings.state.value)

    private fun rebuild(s: AstroSettings) {
        val now = AstroTime.nowUtcMillis()
        val chart = chartCalculator.calculate(
            epochUtcMillis = now,
            latitude = s.homeLatitude,
            longitudeEast = s.homeLongitude,
            zodiacMode = s.zodiacMode,
            houseSystem = s.houseSystem,
        )
        val sun = chart.position(CelestialBody.SUN)
        val moon = chart.position(CelestialBody.MOON)
        val phase = if (sun != null && moon != null) MoonPhase.of(sun.longitude, moon.longitude) else null
        _state.value = DashboardState(
            loading = false,
            chart = chart,
            moonPhase = phase,
            retrogrades = chart.positions.filter { it.retrograde },
            momentLabel = formatNow(now),
            locationLabel = s.homeLabel,
            zodiacLabel = s.zodiacMode.label,
        )
    }

    private fun formatNow(epochMillis: Long): String {
        val dt = Instant.fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        fun p(v: Int) = if (v < 10) "0$v" else "$v"
        return "${p(dt.dayOfMonth)}.${p(dt.monthNumber)}.${dt.year} ${p(dt.hour)}:${p(dt.minute)}"
    }
}
