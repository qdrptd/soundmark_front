package com.example.soundmark.data.dto

import com.google.gson.annotations.SerializedName

data class SpotifyVerifyRequest(
    @SerializedName("spotify_access_token")
    val accessToken: String,

    @SerializedName("spotify_refresh_token")
    val refreshToken: String,

    @SerializedName("expires_in")
    val expiresIn: Int
)