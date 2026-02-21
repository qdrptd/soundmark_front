package com.example.soundmark.data.model

data class ProfileUser(
    val id: String,
    val spotifyId: String,
    val name: String,
    val email: String,
    val profileImage: Int = 1,
    val statusMessage: String? = null,
    val profileImageUrl: String?,
    val followerCount: Int,
    val followingCount: Int,
    val soundMarkCount: Int,
    val isFollowing: Boolean,
    val isFollowedBy: Boolean
) {
    companion object {
        val Default = ProfileUser(
            id = "0",
            spotifyId = "spotify_user",
            name = "사용자",
            email = "user@example.com",
            profileImage = 1,
            statusMessage = "음악과 함께 걷는 여행자",
            profileImageUrl = null,
            followerCount = 0,
            followingCount = 0,
            soundMarkCount = 0,
            isFollowing = false,
            isFollowedBy = false
        )
    }
}