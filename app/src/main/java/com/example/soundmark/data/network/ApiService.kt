package com.example.soundmark.data.network

import com.example.soundmark.data.model.UserProfile
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("me")
    suspend fun getUserProfile(
        @Header("Authorization") authHeader: String
    ): UserProfile
}
