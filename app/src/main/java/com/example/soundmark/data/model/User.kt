package com.example.soundmark.data.model

data class User(
    val id: String,
    val name: String,
    val profileImage: Int = 1,      // 추가: 캐릭터 인덱스
    val statusMessage: String = "", // 추가: 상태 메시지
    val profileImageUrl: String? = null,
    val followerCount: Int,
    val followingCount: Int,
    val soundMarkCount: Int
) {
    companion object {
        val Default = User(
            id = "1234",
            name = "qdrptd",
            profileImage = 1,
            statusMessage = "음악과 함께 걷는 여행자",
            profileImageUrl = null,
            followerCount = 0,
            followingCount = 0,
            soundMarkCount = 1
        )
    }
}