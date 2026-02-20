package com.example.soundmark.data.model

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val placeName: String?
) {
    companion object {
        val Default = GeoLocation(
            latitude = 35.1823,
            longitude = 123.4238,
            placeName = "엘리스랩 성수",
        )
    }
}