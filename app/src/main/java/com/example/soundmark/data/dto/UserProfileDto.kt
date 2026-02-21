package com.example.soundmark.data.dto

import com.example.soundmark.data.model.*
import com.google.gson.annotations.SerializedName

/**
 * /api/v1/users/me 응답을 위한 DTO
 */
data class UserProfileDto(
    val id: Int,
    val spotify_id: String,
    val display_name: String,
    val email: String,
    val created_at: String,
    val follower_count: Int,
    val following_count: Int,
    val recommendation_count: Int,
    val is_following: Boolean,
    val is_followed_by: Boolean,
    val recommendations: List<ProfileRecommendationDto> = emptyList()
)

/**
 * 프로필 내 추천곡 리스트 아이템 DTO
 */
data class ProfileRecommendationDto(
    val id: Int,
    @SerializedName("track_title") val title: String,
    @SerializedName("artist_name") val artist: String,
    @SerializedName("album_cover") val albumCover: String,
    @SerializedName("spotify_link") val link: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("place_name") val placeName: String?,
    val comment: String?,
    @SerializedName("like_count") val likeCount: Int,
    @SerializedName("preview_url") val previewUrl: String?
)

// 1. UserProfileDto -> ProfileUser (상세 정보용) 변환
fun UserProfileDto.toProfileUserDomain(): ProfileUser = ProfileUser(
    id = this.id.toString(),
    spotifyId = this.spotify_id,
    name = this.display_name,
    email = this.email,
    profileImageUrl = null, // 필요 시 확장
    followerCount = this.follower_count,
    followingCount = this.following_count,
    soundMarkCount = this.recommendation_count,
    isFollowing = this.is_following,
    isFollowedBy = this.is_followed_by
)

// 2. UserProfileDto -> User (기존 공통 모델) 변환
fun UserProfileDto.toUserDomain(): User = User(
    id = this.id.toString(),
    name = this.display_name,
    profileImageUrl = null,
    followerCount = this.follower_count,
    followingCount = this.following_count,
    soundMarkCount = this.recommendation_count
)

// 3. ProfileRecommendationDto -> SoundMark 변환
fun ProfileRecommendationDto.toDomain(author: User): SoundMark = SoundMark(
    id = this.id.toString(),
    track = Track(
        title = this.title,
        artist = this.artist,
        albumCoverUrl = this.albumCover,
        spotifyUrl = this.link,
        previewUrl = this.previewUrl
    ),
    author = author,
    location = GeoLocation(
        latitude = this.latitude,
        longitude = this.longitude,
        placeName = this.placeName
    ),
    message = this.comment,
    imageUrls = emptyList(),
    reactions = emptyList(), // 필요 시 likeCount를 기반으로 기본 Reaction 생성
    createdAt = System.currentTimeMillis(), // 리스트 응답에 날짜 정보가 없을 경우 현재 시간
    isActive = true // 내 프로필의 곡은 항상 활성화로 표시
)

// 4. UserProfileDto -> Profile (최종 UI 상태용) 변환
fun UserProfileDto.toProfileDomain(): Profile {
    val userDomain = this.toUserDomain()
    return Profile(
        user = userDomain,
        mySoundMarks = this.recommendations.map { it.toDomain(userDomain) },
        likedSoundMarks = emptyList() // 별도의 API가 필요하므로 초기값은 빈 리스트
    )
}