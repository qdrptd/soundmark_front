package com.example.soundmark.data.repository.user

import android.content.Context
import android.util.Log
import com.example.soundmark.data.dto.SpotifyVerifyRequest
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

    // --- Backend Tokens ---
    override fun saveAccessToken(token: String) {
        Log.d("AuthRepository", "Saving Backend Access Token: ${token.take(10)}...")
        prefs.edit { putString("access_token", token) }
        _isLoggedIn.value = true
    }

    override fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    override fun saveRefreshToken(token: String) {
        Log.d("AuthRepository", "Saving Backend Refresh Token: ${token.take(10)}...")
        prefs.edit { putString("refresh_token", token) }
    }

    override fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    // --- Spotify Tokens ---
    override fun saveSpotifyAccessToken(token: String) {
        Log.d("AuthRepository", "Saving Spotify Access Token: ${token.take(10)}...")
        prefs.edit { putString("spotify_access_token", token) }
    }

    override fun getSpotifyAccessToken(): String? {
        return prefs.getString("spotify_access_token", null)
    }

    override fun saveSpotifyRefreshToken(token: String) {
        Log.d("AuthRepository", "Saving Spotify Refresh Token: ${token.take(10)}...")
        prefs.edit { putString("spotify_refresh_token", token) }
    }

    override fun getSpotifyRefreshToken(): String? {
        return prefs.getString("spotify_refresh_token", null)
    }

    override fun clearSession() {
        prefs.edit { 
            remove("access_token")
            remove("refresh_token")
            remove("spotify_access_token")
            remove("spotify_refresh_token")
            remove("verifier")
        }
        _isLoggedIn.value = false
    }

    override suspend fun handleSpotifyPKCE(
        code: String,
        clientId: String,
        redirectUri: String
    ): Result<String> = runCatching {
        Log.d("AuthRepository", "Starting Spotify PKCE flow with code: $code")
        val verifier = getCodeVerifier() ?: throw Exception("Code verifier is null")

        // 1️⃣ Exchange authorization code for Spotify tokens
        val tokenResponse = spotifyAuthApi.exchangeToken(
            code = code,
            redirectUri = redirectUri,
            clientId = clientId,
            codeVerifier = verifier
        )

        Log.d("AuthRepository", "Spotify tokens received. Saving Spotify Access/Refresh Tokens.")
        saveSpotifyAccessToken(tokenResponse.accessToken)
        tokenResponse.refreshToken?.let { saveSpotifyRefreshToken(it) }

        // 2️⃣ Verify with backend to get JWT
        val verifyRequest = SpotifyVerifyRequest(
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken ?: "",
            expiresIn = tokenResponse.expiresIn
        )
        
        val jwtResponse = apiService.spotifyVerify(verifyRequest)
        Log.d("AuthRepository", "Backend verification successful. JWT received.")

        // 3️⃣ Save backend JWT and refresh token
        saveAccessToken(jwtResponse.accessToken)
        jwtResponse.refreshToken?.let { saveRefreshToken(it) }

        return Result.success(jwtResponse.accessToken)
    }.onFailure { e ->
        Log.e("AuthRepository", "handleSpotifyPKCE failed: ${e.message}", e)
    }

    override suspend fun refreshAccessToken(clientId: String): Result<String> = runCatching {
        val refreshToken = getSpotifyRefreshToken() ?: throw Exception("No Spotify refresh token available")
        Log.d("AuthRepository", "Attempting Spotify token refresh via Spotify API...")
        
        val tokenResponse = spotifyAuthApi.refreshToken(
            refreshToken = refreshToken,
            clientId = clientId
        )

        val newAccessToken = tokenResponse.accessToken
        saveSpotifyAccessToken(newAccessToken)
        tokenResponse.refreshToken?.let { saveSpotifyRefreshToken(it) }
        
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
