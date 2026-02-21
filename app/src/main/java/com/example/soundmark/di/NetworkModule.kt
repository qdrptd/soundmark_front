package com.example.soundmark.di

import com.example.soundmark.data.network.ApiService
import com.example.soundmark.data.network.AuthInterceptor
import com.example.soundmark.data.network.SpotifyApi
import com.example.soundmark.data.network.SpotifyAuthApi
import com.example.soundmark.data.network.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val SPOTIFY_ACCOUNTS_BASE_URL = "https://accounts.spotify.com/"
    private const val SPOTIFY_API_BASE_URL = "https://api.spotify.com/"
    private const val BACKEND_BASE_URL = "http://3.27.56.69/"
    private const val GOOGLE_MAPS_BASE_URL = "https://maps.googleapis.com/"

    // =========================
    // OkHttp
    // =========================

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @Named("authenticatedOkHttpClient")
    fun provideAuthenticatedOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    // =========================
    // Retrofit — Spotify TOKEN (accounts)
    // =========================

    @Provides
    @Singleton
    @Named("spotifyAccounts")
    fun provideSpotifyAccountsRetrofit(
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(SPOTIFY_ACCOUNTS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // =========================
    // Retrofit — Spotify API (v1)
    // =========================

    @Provides
    @Singleton
    @Named("spotifyApi")
    fun provideSpotifyApiRetrofit(
        @Named("authenticatedOkHttpClient") client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(SPOTIFY_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // =========================
    // Retrofit — 우리 서버
    // =========================

    @Provides
    @Singleton
    @Named("backend")
    fun provideBackendRetrofit(
        @Named("authenticatedOkHttpClient") client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("googleMaps")
    fun provideGoogleMapsRetrofit(
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(GOOGLE_MAPS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // =========================
    // API Interfaces
    // =========================

    @Provides
    @Singleton
    fun provideSpotifyAuthApi(
        @Named("spotifyAccounts") retrofit: Retrofit
    ): SpotifyAuthApi =
        retrofit.create(SpotifyAuthApi::class.java)

    @Provides
    @Singleton
    fun provideSpotifyApi(
        @Named("spotifyApi") retrofit: Retrofit
    ): SpotifyApi =
        retrofit.create(SpotifyApi::class.java)

    @Provides
    @Singleton
    fun provideApiService(
        @Named("backend") retrofit: Retrofit
    ): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideMapApiService(
        @Named("googleMaps") retrofit: Retrofit
    ): com.example.soundmark.data.network.MapApiService =
        retrofit.create(com.example.soundmark.data.network.MapApiService::class.java)
}