package com.lloppy.timenomad.screens.transits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lloppy.timenomad.astro.chart.ChartCalculator
import com.lloppy.timenomad.astro.chart.MoonIngress
import com.lloppy.timenomad.astro.chart.TransitCalculator
import com.lloppy.timenomad.astro.chart.TransitContact
import com.lloppy.timenomad.astro.chart.TransitForecaster
import com.lloppy.timenomad.astro.chart.TransitResult
import com.lloppy.timenomad.astro.model.Chart
import com.lloppy.timenomad.astro.time.AstroTime
import com.lloppy.timenomad.data.settings.AstroSettingsRepository
import com.lloppy.timenomad.data.profile.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class TransitsState(
    val title: String = "",
    val momentLabel: String = "",
    val natal: Chart? = null,
    val transit: TransitResult? = null,
    val moonIngresses: List<MoonIngress> = emptyList(),
    val peakDays: List<TransitContact> = emptyList(),
)

class TransitsViewModel(
    private val profileId: String,
    private val chartCalculator: ChartCalculator,
    private val transitCalculator: TransitCalculator,
    private val forecaster: TransitForecaster,
    private val astroSettings: AstroSettingsRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TransitsState())
    val state: StateFlow<TransitsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(astroSettings.state, profileRepository.profiles) { _, _ -> }.collect { rebuild() }
        }
    }

    fun refresh() {
        viewModelScope.launch { rebuild() }
    }

    private suspend fun rebuild() {
        val profile = profileRepository.get(profileId) ?: return
        val s = astroSettings.state.value
        val now = AstroTime.nowUtcMillis()
        val computed = withContext(Dispatchers.Default) {
            val natal = chartCalculator.calculate(
                epochUtcMillis = profile.epochUtcMillis,
                latitude = profile.latitude,
                longitudeEast = profile.longitude,
                zodiacMode = s.zodiacMode,
                houseSystem = s.houseSystem,
            )
            val transit = transitCalculator.calculate(natal, now)
            val ingresses = forecaster.moonIngresses(now, s.zodiacMode, count = 6)
            val peaks = forecaster.moonConjunctions(natal.positions, now, s.zodiacMode, horizonDays = 30)
            TransitsState(
                title = profile.name,
                momentLabel = formatTime(now, withDate = true),
                natal = natal,
                transit = transit,
                moonIngresses = ingresses,
                peakDays = peaks,
            )
        }
        _state.value = computed
    }

    companion object {
        fun formatTime(epochMillis: Long, withDate: Boolean): String {
            val dt = Instant.fromEpochMilliseconds(epochMillis)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            fun p(v: Int) = if (v < 10) "0$v" else "$v"
            val time = "${p(dt.hour)}:${p(dt.minute)}"
            return if (withDate) "${p(dt.dayOfMonth)}.${p(dt.monthNumber)}.${dt.year} $time" else time
        }
    }
}
