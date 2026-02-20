package com.example.soundmark.data.repository.user

interface AuthRepository {
    fun saveAccessToken(token: String)
    fun getAccessToken(): String?
    fun clearSession()


    suspend fun handleSpotifyLogin(
        code: String,
        clientId: String,
        redirectUri: String,
    ): Result<String>

    fun saveCodeVerifier(verifier: String)
    fun getCodeVerifier(): String?
}
