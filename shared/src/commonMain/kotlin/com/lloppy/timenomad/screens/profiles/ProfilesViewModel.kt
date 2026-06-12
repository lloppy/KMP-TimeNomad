package com.lloppy.timenomad.screens.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lloppy.timenomad.data.profile.AstroProfile
import com.lloppy.timenomad.data.profile.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProfilesViewModel(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    val profiles: StateFlow<List<AstroProfile>> =
        combine(repository.profiles, query) { all, q ->
            if (q.isBlank()) all else repository.search(q)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val searchQuery: StateFlow<String> = query

    fun onQueryChange(value: String) { query.value = value }

    fun delete(id: String) = repository.delete(id)
}
