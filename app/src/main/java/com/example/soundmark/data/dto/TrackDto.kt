package com.example.soundmark.data.dto

import com.example.soundmark.data.model.Track

data class TrackDto(
    val id: Int,
    val spotify_track_id: String,
    val title: String,
    val artist: String,
    val album: String,
    val album_cover_url: String,
    val track_url: String,
    val preview_url: String?
)

fun TrackDto.toDomain(): Track {
    return Track(
        id = this.id.toString(),
        title = this.title,
        artist = this.artist,
        albumCoverUrl = this.album_cover_url,
        spotifyUrl = this.track_url, // JSON의 track_url을 spotifyUrl로 매핑
        previewUrl = this.preview_url
    )
}