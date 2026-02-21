package com.example.soundmark.data.repository.songDetail

import com.example.soundmark.data.mock.MockDataSource
import com.example.soundmark.data.model.SoundMark
import javax.inject.Inject

class SoundMarkDetailRepositoryImpl @Inject constructor(): SoundMarkDetailRepository {
    override suspend fun getSoundMarkById(id: String): Result<SoundMark> {
        // TODO: 실제 환경에선 API 통신을 하겠지만, 지금은 Default 값을 반환해
        return try {
            // MockDataSource에서 id를 기반으로 데이터를 가져옵니다.
            val mockData = MockDataSource.getSoundMarkDetail(id)
            Result.success(mockData)
        } catch (e: Exception) {
            // 에러 발생 시 Result.failure로 감싸서 전달합니다.
            Result.failure(e)
        }
    }

    override suspend fun postReaction(id: String, reactionType: String): Result<Unit> {
        return Result.success(Unit)
    }
}