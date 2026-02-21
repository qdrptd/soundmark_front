package com.example.soundmark.data.repository.user

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>
    
    // Backend Tokens
    fun saveAccessToken(token: String)
    fun getAccessToken(): String?
    fun saveRefreshToken(token: String)
    fun getRefreshToken(): String?

    // Spotify Tokens
    fun saveSpotifyAccessToken(token: String)
    fun getSpotifyAccessToken(): String?
    fun saveSpotifyRefreshToken(token: String)
    fun getSpotifyRefreshToken(): String?

    fun clearSession()


    suspend fun handleSpotifyPKCE(
        code: String,
        clientId: String,
        redirectUri: String,
    ): Result<String>

    suspend fun refreshAccessToken(
        clientId: String
    ): Result<String>

    suspend fun refreshBackendToken(): Result<String>

    fun saveCodeVerifier(verifier: String)
    fun getCodeVerifier(): String?
}
