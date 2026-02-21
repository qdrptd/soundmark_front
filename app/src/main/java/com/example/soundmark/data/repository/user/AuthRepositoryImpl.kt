package com.example.soundmark.data.repository.user

import android.content.Context
import android.util.Log
import com.example.soundmark.data.network.ApiService
import com.example.soundmark.data.network.SpotifyAuthApi
import com.example.soundmark.data.network.SpotifyAuthDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val spotifyAuthApi: SpotifyAuthApi,
    private val apiService: ApiService,
    private val spotifyDataSource: SpotifyAuthDataSource,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(getAccessToken() != null)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    override fun saveAccessToken(token: String) {
        Log.d("AuthRepository", "Saving Access Token: ${token.take(10)}...")
        prefs.edit { putString("access_token", token) }
        _isLoggedIn.value = true
    }

    override fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    override fun saveRefreshToken(token: String) {
        Log.d("AuthRepository", "Saving Refresh Token: ${token.take(10)}...")
        prefs.edit { putString("refresh_token", token) }
    }

    override fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    override fun clearSession() {
        prefs.edit { 
            remove("access_token")
            remove("refresh_token")
        }
        _isLoggedIn.value = false
    }

    override suspend fun handleSpotifyLogin(
        code: String,
        clientId: String,
        redirectUri: String,
    ): Result<String> = runCatching {


        Log.d("code", code)
        val verifier = getCodeVerifier()
        Log.d("verifier", verifier.toString())
        if (verifier == null) throw Exception("verifier is null")
        // 1️⃣ code → access token
        val tokenResponse = spotifyAuthApi.exchangeToken(
            code = code,
            redirectUri = redirectUri,
            clientId = clientId,
            codeVerifier = verifier
        )

        Log.d("accessToken", tokenResponse.accessToken)

        val spotifyAccessToken = tokenResponse.accessToken
        saveAccessToken(spotifyAccessToken)
        Log.d("AuthRepository", "Refresh token in response: ${tokenResponse.refreshToken != null}")
        tokenResponse.refreshToken?.let { saveRefreshToken(it) }

        return Result.success(spotifyAccessToken)
    }

    override suspend fun handleSpotifyCallback(code: String): Result<String> = runCatching {
        Log.d("AuthRepository", "Calling spotify/callback with code: $code")
        val response = apiService.spotifyCallback(code)
        
        saveAccessToken(response.accessToken)
        response.refreshToken?.let { saveRefreshToken(it) }
        
        return Result.success(response.accessToken)
    }.onFailure { e ->
        Log.e("AuthRepository", "spotify/callback failed: ${e.message}", e)
    }

    override suspend fun refreshAccessToken(clientId: String): Result<String> = runCatching {
        val refreshToken = getRefreshToken() ?: throw Exception("No refresh token available")
        Log.d("AuthRepository", "Attempting refresh with token: ${refreshToken.take(10)}...")
        
        val tokenResponse = spotifyAuthApi.refreshToken(
            refreshToken = refreshToken,
            clientId = clientId
        )

        val newAccessToken = tokenResponse.accessToken
        saveAccessToken(newAccessToken)
        Log.d("AuthRepository", "New refresh token in response: ${tokenResponse.refreshToken != null}")
        tokenResponse.refreshToken?.let { saveRefreshToken(it) }
        
        return Result.success(newAccessToken)
    }.onFailure { e ->
        Log.e("AuthRepository", "refreshAccessToken failed: ${e.message}", e)
    }

    override fun saveCodeVerifier(verifier: String){
        prefs.edit().putString("verifier", verifier).apply()
    }

    override fun getCodeVerifier(): String?{
        return prefs.getString("verifier", null)
    }
}
