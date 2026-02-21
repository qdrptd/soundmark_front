package com.example.soundmark.data.dto

import com.example.soundmark.data.model.PlaceRecommendationCard
import com.google.gson.annotations.SerializedName

/**
 * 장소 추천 응답 최상위 DTO
 */
data class PlaceRecommendationResponseDto(
    @SerializedName("cards") val cards: List<RecommendationCardDto>
)

/**
 * 개별 추천 카드 정보
 */
data class RecommendationCardDto(
    @SerializedName("track") val track: RemoteTrackDto,
    @SerializedName("matched_genre") val matchedGenre: String,
    @SerializedName("place") val place: RemotePlaceDto,
    @SerializedName("recommendation_count") val recommendationCount: Int
)

/**
 * 카드 내 곡 정보
 */
data class RemoteTrackDto(
    @SerializedName("spotify_track_id") val spotifyTrackId: String,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("album") val album: String,
    @SerializedName("album_cover_url") val albumCoverUrl: String,
    @SerializedName("track_url") val trackUrl: String,
    @SerializedName("genres") val genres: List<String>
)

/**
 * 카드 내 장소 정보
 */
data class RemotePlaceDto(
    @SerializedName("place_id") val placeId: Int,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("address") val address: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

fun RecommendationCardDto.toDomain(): PlaceRecommendationCard {
    return PlaceRecommendationCard(
        trackId = track.spotifyTrackId,
        title = track.title,
        artist = track.artist,
        albumCoverUrl = track.albumCoverUrl,
        spotifyUrl = track.trackUrl,
        matchedGenre = matchedGenre,
        placeId = place.placeId,
        placeName = place.placeName,
        address = place.address,
        latitude = place.lat,
        longitude = place.lng,
        totalRecommendations = recommendationCount
    )
}