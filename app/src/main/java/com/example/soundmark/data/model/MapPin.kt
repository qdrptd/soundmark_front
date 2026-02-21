package com.example.soundmark.data.model

data class MapPin(
    val soundmarkId: String,
    val track: Track,
    val latitude: Double,
    val longitude: Double,
    val isActive: Boolean,
    val count: Int
) {
    companion object {
        val Default = MapPin(
            soundmarkId = "1234",
            track = Track.Default,
            latitude = 37.5665,
            longitude = 126.9780,
            isActive = true,
            count = 2341
        )
    }
}