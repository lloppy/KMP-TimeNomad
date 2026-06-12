package com.lloppy.timenomad.data.profile

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ProfileRepository(private val settings: Settings) {

    private val json = Json { ignoreUnknownKeys = true }
    private val _profiles = MutableStateFlow(load())
    val profiles: StateFlow<List<AstroProfile>> = _profiles.asStateFlow()

    fun get(id: String): AstroProfile? = _profiles.value.firstOrNull { it.id == id }

    fun save(profile: AstroProfile): AstroProfile {
        val withId = if (profile.id.isBlank()) profile.copy(id = newId()) else profile
        val list = _profiles.value.filterNot { it.id == withId.id } + withId
        persist(list.sortedBy { it.name.lowercase() })
        return withId
    }

    fun delete(id: String) {
        persist(_profiles.value.filterNot { it.id == id })
    }

    fun search(query: String): List<AstroProfile> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return _profiles.value
        return _profiles.value.filter { p ->
            p.name.lowercase().contains(q) ||
                p.placeLabel.lowercase().contains(q) ||
                p.tags.any { it.lowercase().contains(q) }
        }
    }

    private fun persist(list: List<AstroProfile>) {
        _profiles.value = list
        settings.putString(KEY, json.encodeToString(list))
    }

    private fun load(): List<AstroProfile> {
        val raw = settings.getStringOrNull(KEY) ?: return emptyList()
        return runCatching { json.decodeFromString<List<AstroProfile>>(raw) }.getOrDefault(emptyList())
    }

    @OptIn(ExperimentalTime::class)
    private fun newId(): String = "p${Clock.System.now().toEpochMilliseconds()}"

    private companion object {
        const val KEY = "profiles.v1"
    }
}
