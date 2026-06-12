package com.lloppy.timenomad.data.geo

data class GeoPlace(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

interface GeocodingService {
    suspend fun search(query: String): Result<List<GeoPlace>>
}

data class PlaceSearchState(
    val loading: Boolean = false,
    val results: List<GeoPlace> = emptyList(),
    val error: String? = null,
)
