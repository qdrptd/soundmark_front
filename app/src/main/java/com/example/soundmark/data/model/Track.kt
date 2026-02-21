package com.example.soundmark.data.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val albumCoverUrl: String,
    val spotifyUrl: String,
    val previewUrl: String?
) {
    companion object {
        val Default = Track(
            id = "123",
            title = "Track title",
            artist = "Artist name",
            albumCoverUrl = "https://example.com/album.jpg",
            spotifyUrl = "https://open.spotify.com/track/123",
            previewUrl = null
        )
    }
}