package com.lloppy.timenomad.screens.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lloppy.timenomad.data.geo.GeoPlace
import com.lloppy.timenomad.data.geo.GeocodingService
import com.lloppy.timenomad.data.profile.AstroProfile
import com.lloppy.timenomad.data.profile.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaceSearchState(
    val loading: Boolean = false,
    val results: List<GeoPlace> = emptyList(),
    val error: String? = null,
)

/** Редактор профиля: предзагрузка, поиск места (геокодер) и сохранение. */
class ProfileEditorViewModel(
    private val profileId: String?,
    private val repository: ProfileRepository,
    private val geocoding: GeocodingService,
) : ViewModel() {

    val initial: AstroProfile? = profileId?.let { repository.get(it) }
    val isNew: Boolean = initial == null

    private val _search = MutableStateFlow(PlaceSearchState())
    val search: StateFlow<PlaceSearchState> = _search.asStateFlow()

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

    fun save(profile: AstroProfile): String =
        repository.save(profile.copy(id = profileId ?: "")).id

    companion object {
        /** Грубая оценка смещения UTC по долготе (без DST/полит. зон), в минутах. */
        fun estimateUtcOffsetMinutes(longitudeEast: Double): Int {
            val hours = (longitudeEast / 15.0)
            return (hours * 60).let { kotlin.math.round(it / 60.0).toInt() * 60 }
        }
    }
}
