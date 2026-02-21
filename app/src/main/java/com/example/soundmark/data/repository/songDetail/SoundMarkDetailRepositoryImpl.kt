package com.example.soundmark.data.repository.songDetail

import com.example.soundmark.data.dto.ErrorResponseDto
import com.example.soundmark.data.dto.toDomain
import com.example.soundmark.data.mock.MockDataSource
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
                // 1. 성공 시 DTO를 도메인 모델로 변환 (날짜 파싱, 이모지 매핑 포함)
                val body = response.body() ?: throw Exception("응답 데이터가 비어있습니다.")
                Result.success(body.toDomain())
            } else {
                // 2. 에러 핸들링 (특히 403 거리 제한 처리)
                if (response.code() == 403) {
                    val errorJson = response.errorBody()?.string()
                    val errorData = gson.fromJson(errorJson, ErrorResponseDto::class.java)
                    // 서버 메시지: "추천곡 상세는 반경 200m 이내에서만 볼 수 있습니다."
                    Result.failure(Exception(errorData.detail.message))
                } else {
                    Result.failure(Exception("데이터 조회 실패 (코드: ${response.code()})"))
                }
            }
        } catch (e: Exception) {
            // 네트워크 끊김, 타임아웃 등
            Result.failure(e)
        }
    }

    override suspend fun postReaction(id: String, reactionType: String): Result<Unit> {
        return Result.success(Unit)
    }
}