package com.example.soundmark.data.repository.recommendation

import android.content.SharedPreferences
import com.example.soundmark.data.dto.toDomain
import com.example.soundmark.data.model.PlaceRecommendationCard
import com.example.soundmark.data.network.ApiService
import com.example.soundmark.data.repository.user.AuthRepository
import javax.inject.Inject

class RecommendationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : RecommendationRepository {

    override suspend fun getPlaceRecommendations(): Result<List<PlaceRecommendationCard>> {
        return runCatching {
            // AuthRepository를 통해 저장된 토큰 가져오기
            val token = authRepository.getSpotifyAccessToken()

            // 🚨 로그를 찍어서 토큰이 정말 있는지 확인하세요!
            android.util.Log.d("SoundMark_Debug", "Spotify Token f rom Prefs: $token")

            if (token.isNullOrEmpty()) {
                throw IllegalStateException("Spotify 토큰이 비어있습니다!")
            }

            android.util.Log.d("SoundMark_Debug", "Sending request with X-Spotify-Token...")

            // API Service의 @Header("X-Spotify-Token")에 토큰 전달
            // 커스텀 헤더이므로 'Bearer ' 접두사 없이 토큰 값만 보냅니다.
            val response = apiService.getPlaceRecommendations(token)

            response.cards.map { it.toDomain() }
        }.onFailure {
            it.printStackTrace()
        }
    }
}