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
        val url = response.request.url.toString()
        Log.d("TokenAuthenticator", "Received 401 for $url")

        // Retry count check (avoid infinite loops)
        if (response.responseCount >= 3) {
            Log.e("TokenAuthenticator", "Retry limit reached for $url, giving up")
            return null
        }

        val authRepository = authRepositoryProvider.get()
        val clientId = BuildConfig.SPOTIFY_CLIENT_ID

        synchronized(this) {
            // 스포티파이 API 호출 중 발생한 401인 경우
            if (url.contains("api.spotify.com")) {
                val currentSpotifyToken = authRepository.getSpotifyAccessToken()
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                Log.d("TokenAuthenticator", "Attempting Spotify token refresh. currentToken=${currentSpotifyToken?.take(5)}, requestToken=${requestToken?.take(5)}")

                // If the token was already refreshed by another thread, use the new one
                if (currentSpotifyToken != null && currentSpotifyToken != requestToken) {
                    Log.d("TokenAuthenticator", "Spotify Token already refreshed by another thread")
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $currentSpotifyToken")
                        .build()
                }

                // Otherwise, refresh the token
                Log.d("TokenAuthenticator", "Refreshing Spotify token via Spotify accounts...")
                val result = runBlocking {
                    authRepository.refreshAccessToken(clientId)
                }

                return if (result.isSuccess) {
                    val newToken = result.getOrThrow()
                    Log.d("TokenAuthenticator", "Spotify Refresh successful! Retrying request with new token.")
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                } else {
                    Log.e("TokenAuthenticator", "Spotify Refresh failed: ${result.exceptionOrNull()?.message}. Clearing session.")
                    authRepository.clearSession()
                    null
                }
            }
            
            // 우리 백엔드 서버 API 호출 중 발생한 401인 경우
            val currentBackendToken = authRepository.getAccessToken()
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            Log.d("TokenAuthenticator", "Attempting Backend token refresh. currentToken=${currentBackendToken?.take(5)}, requestToken=${requestToken?.take(5)}")

            // If the token was already refreshed by another thread, use the new one
            if (currentBackendToken != null && currentBackendToken != requestToken) {
                Log.d("TokenAuthenticator", "Backend Token already refreshed by another thread")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentBackendToken")
                    .build()
            }

            Log.d("TokenAuthenticator", "Refreshing Backend token...")
            val result = runBlocking {
                authRepository.refreshBackendToken()
            }

            return if (result.isSuccess) {
                val newToken = result.getOrThrow()
                Log.d("TokenAuthenticator", "Backend Refresh successful! Retrying request with new token.")
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                Log.e("TokenAuthenticator", "Backend Refresh failed: ${result.exceptionOrNull()?.message}. Clearing session.")
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
