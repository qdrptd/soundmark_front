package com.example.soundmark.data.dto

import com.example.soundmark.data.model.*
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Locale
import java.util.TimeZone

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
    @SerializedName("track_title") val trackTitle: String,
    @SerializedName("track_artist") val trackArtist: String,
    @SerializedName("album_cover_url") val albumCoverUrl: String,
    val message: String?,
    @SerializedName("place_name") val placeName: String?,
    @SerializedName("created_at") val createdAt: String
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
fun ProfileRecommendationDto.toDomain(author: User): SoundMark {
    return SoundMark(
        id = this.id.toString(),
        track = Track(
            title = this.trackTitle,
            artist = this.trackArtist,
            albumCoverUrl = this.albumCoverUrl,
            spotifyUrl = "", // 리스트 응답에 없음 (필요 시 상세 API 호출)
            previewUrl = null,
            id = ""
        ),
        author = author,
        location = GeoLocation(
            latitude = 0.0,  // 리스트 응답에 좌표가 없음
            longitude = 0.0,
            placeName = this.placeName
        ),
        message = this.message ?: "",
        imageUrls = emptyList(),
        reactions = emptyList(), // 리스트 응답에 likeCount가 없으므로 빈 리스트
        createdAt = parseIsoDateTime(this.createdAt),
        isActive = true
    )
}

// 4. UserProfileDto -> Profile (최종 UI 상태용) 변환
fun UserProfileDto.toProfileDomain(): Profile {
    val userDomain = this.toUserDomain()
    return Profile(
        user = userDomain,
        mySoundMarks = this.recommendations.map { it.toDomain(userDomain) },
        likedSoundMarks = emptyList() // 별도의 API가 필요하므로 초기값은 빈 리스트
    )
}

private fun parseIsoDateTime(dateString: String): Long {
    return try {
        // T 이후의 마이크로초(.819626)는 SimpleDateFormat에서 처리가 까다로우므로
        // 밀리초 단위까지만 인식하거나 초까지만 잘라서 파싱합니다.
        val pattern = "yyyy-MM-dd'T'HH:mm:ss"
        val formatter = SimpleDateFormat(pattern, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC") // 서버 기준 타임존 설정
        }

        // "." 뒤의 소수점 데이터를 제외하고 파싱 (하위 호환성을 위한 가장 안전한 방법)
        val truncatedDate = if (dateString.contains(".")) {
            dateString.substringBefore(".")
        } else {
            dateString
        }

        formatter.parse(truncatedDate)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        // 로그를 남겨두면 나중에 디버깅하기 좋습니다.
        System.currentTimeMillis()
    }
}