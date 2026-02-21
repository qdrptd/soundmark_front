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

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val spotifyAuthApi: SpotifyAuthApi,
    private val api: ApiService,
    private val spotifyDataSource: SpotifyAuthDataSource,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun saveAccessToken(token: String) {
        prefs.edit { putString("access_token", token) }
    }

    override fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    override fun clearSession() {
        prefs.edit { remove("access_token") }
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

        return Result.success(spotifyAccessToken)
    }

    override fun saveCodeVerifier(verifier: String){
        prefs.edit().putString("verifier", verifier).apply()
    }

    override fun getCodeVerifier(): String?{
        return prefs.getString("verifier", null)
    }
}
