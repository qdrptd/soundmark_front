package com.example.soundmark.data.repository.profile

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
        try{
            return Result.success(
                listOf(MapPin.Default, MapPin.Default)
            )
        } catch (e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun getSoundMarkById(markId: String): Result<SoundMark> {
        try{
            return Result.success(
                SoundMark.Default
            )
        } catch (e: Exception){
            return Result.failure(e)
        }
    }


}
