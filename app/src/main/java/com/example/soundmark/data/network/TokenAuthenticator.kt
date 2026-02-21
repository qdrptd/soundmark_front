package com.example.soundmark.data.network

import android.util.Log
import com.example.soundmark.BuildConfig
import com.example.soundmark.data.repository.user.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val authRepositoryProvider: Provider<AuthRepository>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "Received 401 for ${response.request.url}")

        // Retry count check (avoid infinite loops)
        if (response.responseCount >= 3) {
            Log.e("TokenAuthenticator", "Retry limit reached, giving up")
            return null
        }

        val authRepository = authRepositoryProvider.get()
        val clientId = BuildConfig.SPOTIFY_CLIENT_ID

        synchronized(this) {
            val currentToken = authRepository.getAccessToken()
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            Log.d("TokenAuthenticator", "Attempting token refresh. currentToken=${currentToken?.take(5)}, requestToken=${requestToken?.take(5)}")

            // If the token was already refreshed by another thread, use the new one
            if (currentToken != null && currentToken != requestToken) {
                Log.d("TokenAuthenticator", "Token already refreshed by another thread, retrying with new token")
                return response.request.newBuilder()
                    .addHeader("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Otherwise, refresh the token
            Log.d("TokenAuthenticator", "Refreshing token...")
            val result = runBlocking {
                authRepository.refreshAccessToken(clientId)
            }

            return if (result.isSuccess) {
                val newToken = result.getOrThrow()
                Log.d("TokenAuthenticator", "Refresh successful! Retrying request with new token: ${newToken.take(10)}...")
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                Log.e("TokenAuthenticator", "Refresh failed: ${result.exceptionOrNull()?.message}. Clearing session.")
                authRepository.clearSession()
                null
            }
        }
    }

    private val Response.responseCount: Int
        get() {
            var result = 1
            var prevResponse = priorResponse
            while (prevResponse != null) {
                result++
                prevResponse = prevResponse.priorResponse
            }
            return result
        }
}
