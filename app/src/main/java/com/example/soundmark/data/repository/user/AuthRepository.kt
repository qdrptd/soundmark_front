package com.example.soundmark.data.repository.user

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>
    fun saveAccessToken(token: String)
    fun getAccessToken(): String?
    fun saveRefreshToken(token: String)
    fun getRefreshToken(): String?
    fun clearSession()


    suspend fun handleSpotifyLogin(
        code: String,
        clientId: String,
        redirectUri: String,
    ): Result<String>

    suspend fun handleSpotifyCallback(
        code: String
    ): Result<String>

    suspend fun refreshAccessToken(
        clientId: String
    ): Result<String>

    fun saveCodeVerifier(verifier: String)
    fun getCodeVerifier(): String?
}
