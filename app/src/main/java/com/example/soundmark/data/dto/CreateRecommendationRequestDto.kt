package com.example.soundmark.data.dto

import com.google.gson.annotations.SerializedName

data class CreateRecommendationRequestDto(

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lng")
    val lng: Double,

    @SerializedName("place")
    val place: PlaceDto,

    @SerializedName("spotify_track_id")
    val spotifyTrackId: String,

    @SerializedName("message")
    val message: String
)