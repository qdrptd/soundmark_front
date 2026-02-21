package com.example.soundmark.data.repository.profile

import com.example.soundmark.data.dto.toDomain
import com.example.soundmark.data.mock.MockDataSource
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.MapPin
import com.example.soundmark.data.model.Profile
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : MarkRepository {
    override suspend fun getNearbyMarks(geoLocation: GeoLocation): Result<List<MapPin>> {
        return try{

            val response = apiService.getNearbyMusic(
                lat = geoLocation.latitude,
                lng = geoLocation.longitude
            )

            if (response.isSuccessful) {
                val body = response.body()
                // 3. DTO 리스트를 Domain Model(MapPin) 리스트로 매핑
                val pins = body?.recommendations?.map { it.toDomain() } ?: emptyList()
                Result.success(pins)
            } else {
                // 서버 에러 응답 처리 (404, 500 등)
                Result.failure(Exception("Server Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}