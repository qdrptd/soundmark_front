package com.example.soundmark.data.repository.songDetail

import com.example.soundmark.data.model.SoundMark

interface SoundMarkDetailRepository {
    // 상세 정보를 조회할 때 현재 내 GPS 좌표를 함께 넘겨야 서버에서 200m 체크가 가능합니다.
    suspend fun getSoundMarkById(id: String, lat: Double, lng: Double): Result<SoundMark>

    suspend fun putReaction(id: String, emoji: String): Result<Unit>

    suspend fun deleteReaction(id: String): Result<Unit>
}
