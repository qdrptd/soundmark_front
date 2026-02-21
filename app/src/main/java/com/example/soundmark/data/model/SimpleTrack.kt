package com.example.soundmark.data.model

data class SimpleTrack(
    val id: String,
    val title: String,
    val artist: String,
) {
    companion object {
        val Default = SimpleTrack(
            id = "123",
            title = "Track title",
            artist = "Artist name",
        )
    }
}