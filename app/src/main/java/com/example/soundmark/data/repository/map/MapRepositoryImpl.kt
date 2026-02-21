package com.example.soundmark.data.repository.map

import com.example.soundmark.BuildConfig
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.network.MapApiService
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapApiService: MapApiService
) : MapRepository {
    override suspend fun searchNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radius: Int
    ): Result<List<GeoLocation>> {
        return try {
            val response = mapApiService.getNearbyPlaces(
                location = "$latitude,$longitude",
                radius = radius,
                apiKey = BuildConfig.MAPS_API_KEY
            )
            if (response.status == "OK") {
                val locations = response.results.map {
                    GeoLocation(
                        latitude = it.geometry.location.lat,
                        longitude = it.geometry.location.lng,
                        placeName = it.name
                    )
                }
                Result.success(locations)
            } else if (response.status == "ZERO_RESULTS") {
                Result.success(emptyList())
            } else {
                Result.failure(Exception("Google Places API Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
