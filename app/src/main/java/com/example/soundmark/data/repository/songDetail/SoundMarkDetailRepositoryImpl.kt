package com.example.soundmark.data.repository.songDetail

import com.example.soundmark.data.model.SoundMark
import javax.inject.Inject

class SoundMarkDetailRepositoryImpl @Inject constructor(): SoundMarkDetailRepository {
    override suspend fun getSoundMarkById(id: String): Result<SoundMark> {
        // 실제 환경에선 API 통신을 하겠지만, 지금은 Default 값을 반환해
        return Result.success(SoundMark.Default.copy(id = id))
    }

    override suspend fun postReaction(id: String, reactionType: String): Result<Unit> {
        return Result.success(Unit)
    }
}