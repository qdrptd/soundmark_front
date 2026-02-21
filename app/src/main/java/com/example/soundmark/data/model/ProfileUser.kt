package com.example.soundmark.data.model

data class ProfileUser(
    val id: String,
    val spotifyId: String,
    val name: String,
    val email: String,
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
            profileImageUrl = null,
            followerCount = 0,
            followingCount = 0,
            soundMarkCount = 0,
            isFollowing = false,
            isFollowedBy = false
        )
    }
}