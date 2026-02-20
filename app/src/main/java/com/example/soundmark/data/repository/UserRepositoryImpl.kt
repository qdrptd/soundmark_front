package com.example.soundmark.data.repository

import android.content.Context
import com.example.soundmark.data.model.UserProfile
import com.example.soundmark.data.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : UserRepository {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun saveAccessToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    override fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    override fun clearSession() {
        prefs.edit().remove("access_token").apply()
    }

    override suspend fun getUserProfile(): Result<UserProfile> {
        val token = getAccessToken() ?: return Result.failure(Exception("No access token found"))
        return try {
            val profile = apiService.getUserProfile("Bearer $token")
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
