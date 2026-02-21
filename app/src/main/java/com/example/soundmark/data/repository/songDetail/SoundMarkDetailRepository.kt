package com.example.soundmark.data.repository.songDetail

import com.example.soundmark.data.model.SoundMark

interface SoundMarkDetailRepository {
    suspend fun getSoundMarkById(id: String): Result<SoundMark>
    suspend fun postReaction(id: String, reactionType: String): Result<Unit>
}