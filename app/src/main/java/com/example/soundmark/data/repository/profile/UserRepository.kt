package com.example.soundmark.data.repository.profile

import com.example.soundmark.data.model.Profile

interface UserRepository {
    suspend fun getMyProfile(): Result<Profile>

    suspend fun getProfileByUserId(userId: String): Result<Profile>
}
