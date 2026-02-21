package com.example.soundmark.data.network

import com.example.soundmark.data.dto.JwtResponse
import com.example.soundmark.data.dto.MapResponseDto
import com.example.soundmark.data.dto.SpotifyVerifyRequest
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

    @GET("profile/me")
    suspend fun getMyProfile(): Profile

    @GET("profile/{userId}")
    suspend fun getProfileByUserId(
        @Path("userId") userId: String
    ): Profile

    @POST("api/v1/auth/spotify/verify")
    suspend fun spotifyVerify(
        @Body request: SpotifyVerifyRequest
    ): JwtResponse

    @GET("map/nearby")
    suspend fun getNearbyMusic(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<MapResponseDto>
}
