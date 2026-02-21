package com.example.soundmark.data.repository.spotify

import com.example.soundmark.data.model.SimpleTrack
import com.example.soundmark.data.model.Track

interface SpotifyRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
    suspend fun getPopularTracks(): Result<List<SimpleTrack>>
}
