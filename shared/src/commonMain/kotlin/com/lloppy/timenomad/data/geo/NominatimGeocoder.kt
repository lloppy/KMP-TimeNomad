package com.lloppy.timenomad.data.geo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class NominatimGeocoder(
    private val client: HttpClient,
) : GeocodingService {

    override suspend fun search(query: String): Result<List<GeoPlace>> {
        if (query.isBlank()) return Result.success(emptyList())
        return runCatching {
            val response: HttpResponse = client.get(SEARCH_URL) {
                parameter("q", query)
                parameter("format", "jsonv2")
                parameter("limit", "5")
                parameter("accept-language", "ru")
                header(HttpHeaders.UserAgent, USER_AGENT)
            }
            if (!response.status.isSuccess()) {
                error("Geocoder ${response.status.value}: ${response.bodyAsText().take(180)}")
            }
            val places: List<NominatimPlace> = response.body()
            places.mapNotNull { it.toGeoPlace() }
        }
    }

    private fun NominatimPlace.toGeoPlace(): GeoPlace? {
        val la = lat.toDoubleOrNull() ?: return null
        val lo = lon.toDoubleOrNull() ?: return null
        return GeoPlace(name = (displayName ?: "$la, $lo"), latitude = la, longitude = lo)
    }

    private companion object {
        const val SEARCH_URL = "https://nominatim.openstreetmap.org/search"
        const val USER_AGENT = "TimeNomad/1.0 (Kotlin Multiplatform astrology app)"
    }
}

@Serializable
private data class NominatimPlace(
    @SerialName("display_name") val displayName: String? = null,
    val lat: String,
    val lon: String,
)
