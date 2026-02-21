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
        
        Log.d("AuthInterceptor", "Intercepting request to: $url")

        val authRepository = try {
            authRepositoryProvider.get()
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Failed to get AuthRepository: ${e.message}")
            return chain.proceed(originalRequest)
        }

        // URL에 따라 적절한 토큰 선택
        val token = when {
            // 1. 스포티파이 API 호출 시
            url.contains("api.spotify.com") -> {
                val t = authRepository.getSpotifyAccessToken()
                Log.d("AuthInterceptor", "Spotify API call. Token present: ${t != null}")
                t
            }
            // 2. 스포티파이 인증/토큰 갱신 호출 시 (헤더 불필요)
            url.contains("accounts.spotify.com") -> {
                Log.d("AuthInterceptor", "Spotify Accounts call. Skipping header.")
                null
            }
            // 3. 우리 백엔드 토큰 갱신 호출 시 (헤더 불필요)
            url.contains("api/v1/auth/refresh") -> {
                Log.d("AuthInterceptor", "Backend Token Refresh call. Skipping header.")
                null
            }
            // 4. 기타 우리 백엔드 API 호출 시
            else -> {
                val t = authRepository.getAccessToken()
                Log.d("AuthInterceptor", "Backend API call. Token present: ${t != null}")
                t
            }
        }

        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return try {
            Log.d("AuthInterceptor", "Proceeding with request to: $url")
            chain.proceed(requestBuilder.build())
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Chain proceed failed: ${e.message}")
            throw e
        }
    }
}
