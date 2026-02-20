package com.example.soundmark.data.repository

import com.example.soundmark.data.model.SoundMark
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getNearbyMusicMarks(userLocation: LatLng): Flow<List<SoundMark>>
    suspend fun uploadMusicMark(mark: SoundMark): Result<Unit>
}