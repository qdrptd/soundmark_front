package com.example.soundmark.data.dto

import com.example.soundmark.data.model.MapPin

data class RecommendationDto(
    val id: Long,
    val lat: Double,
    val lng: Double,
    val distance_meters: Double,
    val is_active: Boolean,
    val track: TrackDto,
    val user: UserDto,
    val total_reactions: Int
)

fun RecommendationDto.toDomain(): MapPin {
    return MapPin(
        soundmarkId = this.id.toString(),
        track = this.track.toDomain(), // 위에서 만든 확장 함수 사용
        latitude = this.lat,
        longitude = this.lng,
        isActive = this.is_active,
        count = this.total_reactions // 반응 합계를 count로 매핑
    )
}