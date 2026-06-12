package com.lloppy.timenomad.data.geo

/** Найденное место: название/адрес и координаты. */
data class GeoPlace(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

/** Поиск места по строке запроса (геокодинг). Сетевой; используется только при настройке профиля. */
interface GeocodingService {
    suspend fun search(query: String): Result<List<GeoPlace>>
}
