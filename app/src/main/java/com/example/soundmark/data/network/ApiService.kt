package com.example.soundmark.data.network

import com.example.soundmark.data.dto.JwtResponse
import com.example.soundmark.data.dto.MapResponseDto
import com.example.soundmark.data.model.Profile
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("me")
    suspend fun getUserProfile(
        @Header("Authorization") authHeader: String
    ): Profile

    @POST("auth/spotify")
    suspend fun loginWithSpotify(
        @Header("Authorization") authorization: String
    ): JwtResponse

    @GET("map/nearby")
    suspend fun getNearbyMusic(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<MapResponseDto>
}
