package com.example.soundmark.data.repository.profile

import com.example.soundmark.data.dto.toDomain
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.MapPin
import com.example.soundmark.data.network.ApiService
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class MarkRepositoryImpl @Inject constructor(
    private val apiServiceProvider: Provider<ApiService>, // Provider로 변경
) : MarkRepository {
    private val apiService get() = apiServiceProvider.get()

    override suspend fun getNearbyMarks(geoLocation: GeoLocation, userLocation: GeoLocation): Result<List<MapPin>> {
        return try{

            val response = apiService.getNearbyMusic(
                lat = geoLocation.latitude,
                lng = geoLocation.longitude,
                myLat = userLocation.latitude,
                myLng = userLocation.longitude
            )

            if (response.isSuccessful) {
                val body = response.body()
                val pins = body?.recommendations?.map { it.toDomain() } ?: emptyList()
                Result.success(pins)
            } else {
                Result.failure(Exception("Server Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}
