package com.example.soundmark.data.repository.profile

import com.example.soundmark.data.model.Profile
import com.example.soundmark.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : UserRepository {

    override suspend fun getMyProfile(): Result<Profile> {
        try{
            return Result.success(
                Profile.Default
            )
        } catch (e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun getProfileByUserId(userId: String): Result<Profile> {
        try{
            return Result.success(
                Profile.Default
            )
        } catch (e: Exception){
            return Result.failure(e)
        }
    }


}
