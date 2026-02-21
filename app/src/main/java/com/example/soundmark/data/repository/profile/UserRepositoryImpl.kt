package com.example.soundmark.data.repository.profile

import com.example.soundmark.data.model.Profile
import com.example.soundmark.data.model.User
import com.example.soundmark.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : UserRepository {

    override suspend fun getMe(): Result<User> {
        return try {
            val user = apiService.getMe().toDomain()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyProfile(): Result<Profile> {
        return try {
            val profile = apiService.getMyProfile()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfileByUserId(userId: String): Result<Profile> {
        return try {
            val profile = apiService.getProfileByUserId(userId)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
