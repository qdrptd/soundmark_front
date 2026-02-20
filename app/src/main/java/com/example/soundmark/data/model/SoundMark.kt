package com.example.soundmark.data.model

import com.google.android.gms.maps.model.LatLng

data class SoundMark(
    val id: String,
    val title: String,
    val artist: String,
    val albumCoverUrl: String,
    val location: LatLng,
    val recommendationMessage: String,
    val spotifyLink: String,
    val userId: String,
    val likes: Int = 0,
    val isActivated: Boolean = false
)