package com.example.soundmark.data.repository

import com.example.soundmark.data.model.UserProfile

interface UserRepository {
    fun saveAccessToken(token: String)
    fun getAccessToken(): String?
    suspend fun getUserProfile(): Result<UserProfile>
    fun clearSession()
}
