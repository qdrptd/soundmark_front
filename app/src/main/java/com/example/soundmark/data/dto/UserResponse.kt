package com.example.soundmark.data.dto

import com.example.soundmark.data.model.User
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("spotify_id")
    val spotifyId: String,

    @SerializedName("display_name")
    val displayName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("profile_image_url")
    val profileImageUrl: String,

    @SerializedName("created_at")
    val createdAt: String
){
    fun toDomain(): User {
        return User(
            id = id.toString(),
            name = displayName,
            profileImageUrl = profileImageUrl,
            followerCount = 0,
            followingCount = 0,
            soundMarkCount = 0,
        )
    }
}