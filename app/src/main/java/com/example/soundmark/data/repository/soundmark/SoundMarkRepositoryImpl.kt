package com.example.soundmark.data.repository.soundmark

import com.example.soundmark.data.dto.CreateRecommendationRequestDto
import com.example.soundmark.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundMarkRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SoundMarkRepository {

    override suspend fun postRecommendation(request: CreateRecommendationRequestDto): Result<Unit> {
        return try {
            val response = apiService.postRecommendation(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to post recommendation: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
