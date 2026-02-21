package com.example.soundmark.data.repository.soundmark

import com.example.soundmark.data.dto.CreateRecommendationRequestDto

interface SoundMarkRepository {
    suspend fun postRecommendation(request: CreateRecommendationRequestDto): Result<Unit>
}
