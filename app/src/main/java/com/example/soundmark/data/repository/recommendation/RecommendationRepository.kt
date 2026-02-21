package com.example.soundmark.data.repository.recommendation

import com.example.soundmark.data.model.PlaceRecommendationCard

interface RecommendationRepository {
    /**
     * 사용자의 최근 재생 곡 기반 장소 추천 카드 리스트를 가져옵니다.
     * @return Result 객체로 감싼 추천 카드 리스트 (성공/실패 처리 용이)
     */
    suspend fun getPlaceRecommendations(): Result<List<PlaceRecommendationCard>>
}