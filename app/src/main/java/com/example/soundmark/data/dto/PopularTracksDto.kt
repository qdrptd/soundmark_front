package com.example.soundmark.data.dto

import com.example.soundmark.data.model.SimpleTrack
import com.example.soundmark.data.model.Track
import com.google.gson.annotations.SerializedName

data class TracksResponseDto(
    @SerializedName("tracks")
    val tracks: List<SimpleTrackDto>,

    @SerializedName("total")
    val total: Int
){
    fun toDomain(): List<SimpleTrack> {
        return tracks.map { track ->
            SimpleTrack(
                id = track.spotifyTrackId,
                title = track.title,
                artist = track.artist)
        }
    }
}

data class SimpleTrackDto(
    @SerializedName("spotify_track_id")
    val spotifyTrackId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("artist")
    val artist: String,

    @SerializedName("recommendation_count")
    val recommendationCount: Int
)