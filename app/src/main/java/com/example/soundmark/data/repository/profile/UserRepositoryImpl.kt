package com.example.soundmark.data.repository.profile

import com.example.soundmark.data.dto.toProfileDomain
import com.example.soundmark.data.model.Profile
import com.example.soundmark.data.model.User
import com.example.soundmark.data.network.ApiService
import com.example.soundmark.data.dto.toDomain
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiServiceProvider: Provider<ApiService>, // Provider로 변경
) : UserRepository {

    private val apiService get() = apiServiceProvider.get()

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
            val response = apiService.getMyProfile()
            // DTO를 도메인 모델 Profile로 변환
            Result.success(response.toProfileDomain())
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
