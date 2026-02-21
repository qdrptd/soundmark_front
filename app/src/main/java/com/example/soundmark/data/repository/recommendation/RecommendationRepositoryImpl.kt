package com.example.soundmark.data.repository.recommendation

import com.example.soundmark.data.dto.toDomain
import com.example.soundmark.data.model.PlaceRecommendationCard
import com.example.soundmark.data.network.ApiService
import javax.inject.Inject

class RecommendationRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : RecommendationRepository {

    override suspend fun getPlaceRecommendations(): Result<List<PlaceRecommendationCard>> {
        return runCatching {
            // API 호출
            val response = apiService.getPlaceRecommendations()

            // DTO 리스트를 Domain 모델 리스트로 변환 (Mapper 활용)
            response.cards.map { it.toDomain() }
        }.onFailure {
            // 에러 로그 출력 또는 에러 핸들링
            it.printStackTrace()
        }
    }
}