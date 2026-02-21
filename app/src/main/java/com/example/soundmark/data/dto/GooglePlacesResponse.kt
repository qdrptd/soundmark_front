package com.example.soundmark.data.dto

import com.google.gson.annotations.SerializedName

data class GooglePlacesResponse(
    @SerializedName("results") val results: List<PlaceResult>,
    @SerializedName("status") val status: String
)

data class PlaceResult(
    @SerializedName("name") val name: String,
    @SerializedName("geometry") val geometry: Geometry,
    @SerializedName("vicinity") val vicinity: String?,
    @SerializedName("place_id") val placeId: String
)

data class Geometry(
    @SerializedName("location") val location: LocationDto
)

data class LocationDto(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)
