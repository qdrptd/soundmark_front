package com.example.soundmark.data.dto

import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.Reaction
import com.example.soundmark.data.model.ReactionType
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.model.User
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

data class RecommendationDetailDto(
    val id: Int,
    val lat: Double,
    val lng: Double,
    val distance_meters: Double,
    val track: TrackDto, // 기존에 정의한 TrackDto 재사용
    val user: UserDto,   // 기존에 정의한 UserDto 재사용
    val message: String,
    val place_name: String,
    val address: String,
    val created_at: String,
    val reactions: Map<String, Int>, // "❤️": 3 형태의 맵
    val user_reaction: String?       // 내가 누른 이모지 (없을 수 있음)
)

/* 에러 응답 DTO (403 거리 초과 시 사용) */
data class ErrorResponseDto(
    val detail: ErrorDetailDto
)

data class ErrorDetailDto(
    val code: String,
    val message: String,
    val distance_meters: Double
)

// 1. UserDto -> User (도메인) 변환
fun UserDto.toDomain(): User = User(
    id = this.id.toString(),
    name = this.display_name,
    profileImageUrl = null, // DTO에 프로필 이미지 URL이 추가되면 매핑
    followerCount = 0,
    followingCount = 0,
    soundMarkCount = 0
)

fun RecommendationDetailDto.toDomain(): SoundMark {
    // 1. 날짜 변환 (String "2026-02-20T10:30:00" -> Long Epoch)
// 1. 날짜 변환 (SimpleDateFormat 사용)
    val epochMillis = try {
        // 서버 포맷에 맞춰 패턴 설정 (T 구분자 포함)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        // 서버 시간이 UTC라면 아래 코드 추가
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.parse(this.created_at)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }

// 2. 이모지 Map<String, Int> -> List<Reaction> 변환 로직
    val reactionList = this.reactions.map { (emojiStr, count) ->
        // MAIN_TYPES에 정의된 이모지인지 확인하고, 없으면 fromEmoji로 생성
        val type = ReactionType.MAIN_TYPES.find { it.emoji == emojiStr }
            ?: ReactionType.fromEmoji(emojiStr)

        Reaction(
            type = type,
            count = count,
            isReactedByMe = emojiStr == this.user_reaction
        )
    }

    return SoundMark(
        id = this.id.toString(),
        track = this.track.toDomain(), // 기존 TrackDto -> Track 매퍼 활용
        author = this.user.toDomain(), // 위에서 정의한 User 매퍼
        location = GeoLocation(
            latitude = this.lat,
            longitude = this.lng,
            placeName = this.place_name // address 정보는 도메인 모델에 따라 포함 여부 결정
        ),
        message = this.message,
        imageUrls = emptyList(),
        reactions = reactionList,
        createdAt = epochMillis,
        isActive = this.distance_meters <= 200.0 // 200m 이내면 활성화
    )
}