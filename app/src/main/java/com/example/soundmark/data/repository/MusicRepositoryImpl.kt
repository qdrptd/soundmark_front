package com.example.soundmark.data.repository

import com.example.soundmark.data.model.SoundMark
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor() : MusicRepository {
    override fun getNearbyMusicMarks(userLocation: LatLng): Flow<List<SoundMark>> = flow {
        // TODO: 현재는 샘플 데이터 반환
        val mockData = listOf(
            SoundMark("1", "Hype Boy", "NewJeans", "https://example.com/1.jpg", LatLng(37.5665, 126.9780), "오늘 날씨랑 딱!", "spotify:track:1", "userA", 1),
            SoundMark("2", "Ditto", "NewJeans", "https://example.com/2.jpg", LatLng(37.5670, 126.9790), "겨울엔 역시..", "spotify:track:2", "userB", 2)
        )
        emit(mockData)
    }

    override suspend fun uploadMusicMark(mark: SoundMark): Result<Unit> {
        // 데이터 업로드 로직
        return Result.success(Unit)
    }
}