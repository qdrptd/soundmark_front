package com.example.soundmark.data.repository.spotify

import android.util.Log
import com.example.soundmark.data.model.SimpleTrack
import com.example.soundmark.data.model.Track
import com.example.soundmark.data.network.ApiService
import com.example.soundmark.data.network.SpotifyApi
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class SpotifyRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi,
    private val apiServiceProvider: Provider<ApiService>
) : SpotifyRepository {
    private val apiService get() = apiServiceProvider.get()

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = spotifyApi.searchTracks(query)
            val tracks = response.tracks.items.map { dto ->
                Track(
                    id = dto.id,
                    title = dto.name,
                    artist = dto.artists.firstOrNull()?.name ?: "Unknown",
                    albumCoverUrl = dto.album.images.firstOrNull()?.url ?: "",
                    spotifyUrl = dto.externalUrls["spotify"] ?: "",
                    previewUrl = dto.previewUrl
                )
            }
            Result.success(tracks)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("SpotifyRepository", "Search failed with status ${e.code()}: $errorBody")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("SpotifyRepository", "Search failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getPopularTracks(): Result<List<SimpleTrack>> {
        return try {
            val response = apiService.getPopularTracks()
            if (response.isSuccessful) {
                val tracks = response.body()?.toDomain() ?: emptyList()
                Result.success(tracks)
            } else {
                Result.failure(Exception("Failed to get popular tracks: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SpotifyRepository", "getPopularTracks failed: ${e.message}")
            Result.failure(e)
        }
    }
}
