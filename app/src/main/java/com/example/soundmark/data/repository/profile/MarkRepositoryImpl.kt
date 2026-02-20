package com.example.soundmark.data.repository.profile

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
            // TODO: 서버 연결
            Result.success(MockDataSource.getNearbyPins())
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getSoundMarkById(markId: String): Result<SoundMark> {
        return try{
            Result.success(
                SoundMark.Default
            )
        } catch (e: Exception){
            Result.failure(e)
        }
    }


}
