package com.example.soundmark.data.dto

import com.google.gson.annotations.SerializedName

data class SpotifySearchResponse(
    @SerializedName("tracks")
    val tracks: SpotifyTracksDto
)

data class SpotifyTracksDto(
    @SerializedName("items")
    val items: List<SpotifyTrackDto>
)

data class SpotifyTrackDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("artists")
    val artists: List<SpotifyArtistDto>,
    @SerializedName("album")
    val album: SpotifyAlbumDto,
    @SerializedName("external_urls")
    val externalUrls: Map<String, String>,
    @SerializedName("preview_url")
    val previewUrl: String?
)

data class SpotifyArtistDto(
    @SerializedName("name")
    val name: String
)

data class SpotifyAlbumDto(
    @SerializedName("images")
    val images: List<SpotifyImageDto>
)

data class SpotifyImageDto(
    @SerializedName("url")
    val url: String
)
