package com.example.soundmark.data.network

import android.util.Log
import com.example.soundmark.data.repository.user.AuthRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authRepositoryProvider: Provider<AuthRepository>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()
        val authRepository = authRepositoryProvider.get()

        // URL에 따라 적절한 토큰 선택
        val token = when {
            url.contains("api.spotify.com") -> {
                Log.d("AuthInterceptor", "Target: Spotify API. Fetching Spotify Access Token.")
                authRepository.getSpotifyAccessToken()
            }
            url.contains("accounts.spotify.com") -> {
                Log.d("AuthInterceptor", "Target: Spotify Accounts. No Auth Header needed.")
                null
            }
            else -> {
                Log.d("AuthInterceptor", "Target: Backend API. Fetching Backend Access Token.")
                authRepository.getAccessToken()
            }
        }

        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            Log.d("AuthInterceptor", "Attaching token to request: ${originalRequest.url}")
            requestBuilder.header("Authorization", "Bearer $token")
        } else {
            Log.w("AuthInterceptor", "No token attached for request: ${originalRequest.url}")
        }

        return chain.proceed(requestBuilder.build())
    }
}
