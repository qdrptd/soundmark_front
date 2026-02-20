package com.example.soundmark.data.network

import com.example.soundmark.data.model.UserProfile
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("me")
    suspend fun getUserProfile(
        @Header("Authorization") authHeader: String
    ): UserProfile

    @GET("me/tracks")
    suspend fun getSavedTracks(
        @Header("Authorization") authHeader: String
    ): Any // 임시 응답 타입
}
