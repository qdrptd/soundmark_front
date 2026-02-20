package com.example.soundmark.data.repository.user

import android.content.Context
import com.example.soundmark.data.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : AuthRepository {

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
}
