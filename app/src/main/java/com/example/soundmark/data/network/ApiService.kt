package com.example.soundmark.data.network

import com.example.soundmark.data.dto.CreateRecommendationRequestDto
import com.example.soundmark.data.dto.JwtResponse
import com.example.soundmark.data.dto.MapResponseDto
import com.example.soundmark.data.dto.ProfileUpdateRequestDto
import com.example.soundmark.data.dto.RecommendationDetailDto
import com.example.soundmark.data.dto.SpotifyVerifyRequest
import com.example.soundmark.data.dto.UserProfileDto
import com.example.soundmark.data.dto.UserResponse
import com.example.soundmark.data.model.Profile
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import com.example.soundmark.data.model.User
import retrofit2.http.*

interface ApiService {
    @GET("/auth/me")
    suspend fun getMe(): UserResponse

    @GET("api/v1/users/me")
    suspend fun getMyProfile(): UserProfileDto

    @PATCH("api/v1/users/me")
    suspend fun updateMyProfile(
        @Body request: ProfileUpdateRequestDto
    ): Response<UserProfileDto> // 리턴은 기존에 만든 UserProfileDto 재사용

    @GET("api/v1/users/{userId}")
    suspend fun getProfileByUserId(
        @Path("userId") userId: String
    ): Profile

    @POST("api/v1/auth/spotify/verify")
    suspend fun spotifyVerify(
        @Body request: SpotifyVerifyRequest
    ): JwtResponse

    @POST("api/v1/auth/refresh")
    suspend fun refreshBackendToken(
        @Body body: Map<String, String> // { "refresh_token": "..." }
    ): JwtResponse

    @GET("api/v1/map/nearby")
    suspend fun getNearbyMusic(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("my_lat") myLat: Double, // 사용자의 실제 위도 (GPS)
        @Query("my_lng") myLng: Double  // 사용자의 실제 경도 (GPS)
    ): Response<MapResponseDto>

    // 2. 추천곡 상세 조회 (추가된 부분)
    // 상세 조회 시에도 서버에서 200m 거리 체크를 수행하므로 내 GPS 좌표를 보냅니다.
    @GET("api/v1/recommendations/{recommendation_id}")
    suspend fun getRecommendationDetail(
        @Path("recommendation_id") id: String,
        @Query("mylat") myLat: Double,
        @Query("mylng") myLng: Double
    ): Response<RecommendationDetailDto>

    // 3. 리액션 전송 (추가된 부분)
    @POST("api/v1/recommendations/{recommendation_id}/reactions")
    suspend fun postReaction(
        @Path("recommendation_id") id: String,
        @Body body: Map<String, String> // { "reaction_type": "🔥" }
    ): Response<Unit>


    @POST("api/v1/recommendations")
    suspend fun postRecommendation(
        @Body body: CreateRecommendationRequestDto
    ): Response<Unit>
}
