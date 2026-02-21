package com.example.soundmark.data.repository.map

import com.example.soundmark.data.model.GeoLocation

interface MapRepository {
    suspend fun searchNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radius: Int = 1000
    ): Result<List<GeoLocation>>
}
