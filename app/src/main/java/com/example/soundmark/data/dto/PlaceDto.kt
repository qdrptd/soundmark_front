package com.example.soundmark.data.dto

import com.google.gson.annotations.SerializedName

data class PlaceDto(

    @SerializedName("source")
    val source: String,

    @SerializedName("google_place_id")
    val googlePlaceId: String,

    @SerializedName("place_name")
    val placeName: String,

    @SerializedName("address")
    val address: String
)