package com.example.soundmark.data.network

import com.example.soundmark.data.dto.JwtResponse
import com.example.soundmark.data.dto.SpotifyVerifyRequest
import com.example.soundmark.data.dto.UserResponse
import com.example.soundmark.data.model.Profile
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
}
