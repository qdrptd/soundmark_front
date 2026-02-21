package com.example.soundmark.data.repository.spotify

import com.example.soundmark.data.model.Track

interface SpotifyRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}
