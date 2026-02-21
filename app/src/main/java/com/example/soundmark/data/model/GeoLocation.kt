package com.example.soundmark.data.model

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val placeName: String?
) {
    companion object {
        val Default = GeoLocation(
            latitude = 37.5451,
            longitude = 127.0572,
            placeName = "엘리스랩 성수",
        )
    }
}