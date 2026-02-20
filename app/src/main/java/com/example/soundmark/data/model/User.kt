package com.example.soundmark.data.model

data class User(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val followerCount: Int,
    val followingCount: Int,
    val soundMarkCount: Int
) {
    companion object {
        val Default = User(
            id = "1234",
            name = "qdrptd",
            profileImageUrl = null,
            followerCount = 0,
            followingCount = 0,
            soundMarkCount = 1
        )
    }
}