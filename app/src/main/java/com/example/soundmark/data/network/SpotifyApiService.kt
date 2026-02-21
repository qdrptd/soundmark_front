package com.example.soundmark.data.network

import com.example.soundmark.data.dto.SpotifySearchResponse
import com.example.soundmark.data.dto.SpotifyTokenResponse
import retrofit2.http.*


interface SpotifyApi {

    @GET("v1/search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 10
    ): SpotifySearchResponse

}

interface SpotifyAuthApi {

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun exchangeToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Field("code_verifier") codeVerifier: String
    ): SpotifyTokenResponse

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun refreshToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String
    ): SpotifyTokenResponse

}