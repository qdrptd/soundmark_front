package com.example.soundmark.data.repository.songDetail

import com.example.soundmark.data.dto.ErrorResponseDto
import com.example.soundmark.data.dto.toDomain
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.network.ApiService
import com.google.gson.Gson
import javax.inject.Inject

class SoundMarkDetailRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson
): SoundMarkDetailRepository {
    override suspend fun getSoundMarkById(id: String, lat: Double, lng: Double): Result<SoundMark> {
        return try {
            val response = apiService.getRecommendationDetail(id, lat, lng)

            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("응답 데이터가 비어있습니다.")
                Result.success(body.toDomain())
            } else {
                if (response.code() == 403) {
                    val errorJson = response.errorBody()?.string()
                    val errorData = gson.fromJson(errorJson, ErrorResponseDto::class.java)
                    Result.failure(Exception(errorData.detail.message))
                } else {
                    Result.failure(Exception("데이터 조회 실패 (코드: ${response.code()})"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun putReaction(id: String, emoji: String): Result<Unit> {
        return try {
            val body = mapOf("emoji" to emoji)
            val response = apiService.putReaction(id, body)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("리액션 전송 실패 (코드: ${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReaction(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteReaction(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("리액션 삭제 실패 (코드: ${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
