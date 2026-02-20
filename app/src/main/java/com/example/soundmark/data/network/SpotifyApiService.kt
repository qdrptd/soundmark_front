package com.example.soundmark.data.network

import com.example.soundmark.data.dto.SpotifyTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface SpotifyApi {

//    @GET("v1/me")
//    suspend fun getMe(
//        @Header("Authorization") token: String
//    ): SpotifyUserDto
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

}