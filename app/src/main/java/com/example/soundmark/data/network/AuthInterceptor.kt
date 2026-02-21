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
        val token = authRepositoryProvider.get().getAccessToken()

        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            Log.d("AuthInterceptor", "Attaching token to request: ${originalRequest.url}")
            requestBuilder.header("Authorization", "Bearer $token")
        } else {
            Log.w("AuthInterceptor", "No token available for request: ${originalRequest.url}")
        }

        return chain.proceed(requestBuilder.build())
    }
}
