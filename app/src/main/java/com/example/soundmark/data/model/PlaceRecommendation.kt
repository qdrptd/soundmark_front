package com.example.soundmark.data.model

data class PlaceRecommendationCard(
    val trackId: String,
    val title: String,
    val artist: String,
    val albumCoverUrl: String,
    val spotifyUrl: String,
    val matchedGenre: String,
    val placeId: Int,
    val placeName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val totalRecommendations: Int
)