package com.example.soundmark.data.repository.profile

import com.example.soundmark.data.model.Profile
import com.example.soundmark.data.model.User

interface UserRepository {
    suspend fun getMe(): Result<User>

    suspend fun getMyProfile(): Result<Profile>

    suspend fun getProfileByUserId(userId: String): Result<Profile>
    suspend fun updateProfile(name: String?, imageId: Int?, message: String?): Result<User>
}
