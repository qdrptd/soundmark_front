package com.example.soundmark.data.repository.spotify

import com.example.soundmark.data.model.Track
import com.example.soundmark.data.network.SpotifyApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi
) : SpotifyRepository {
    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = spotifyApi.searchTracks(query)
            val tracks = response.tracks.items.map { dto ->
                Track(
                    title = dto.name,
                    artist = dto.artists.firstOrNull()?.name ?: "Unknown",
                    albumCoverUrl = dto.album.images.firstOrNull()?.url ?: "",
                    spotifyUrl = dto.externalUrls["spotify"] ?: "",
                    previewUrl = dto.previewUrl
                )
            }
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
