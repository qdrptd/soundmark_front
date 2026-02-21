package com.example.soundmark.data.mock

import com.example.soundmark.data.model.*

object MockDataSource {

    // 1. 공통으로 쓸 가짜 트랙들
    val mockTracks = listOf(
        Track(
            title = "Hype Boy",
            artist = "NewJeans",
            albumCoverUrl = "https://picsum.photos/200/300",
            spotifyUrl = "spotify:track:1",
            previewUrl = null
        ),
        Track(
            title = "Ditto",
            artist = "NewJeans",
            albumCoverUrl = "https://picsum.photos/200/300",
            spotifyUrl = "spotify:track:2",
            previewUrl = null
        ),
        Track(
            title = "Seven",
            artist = "Jungkook",
            albumCoverUrl = "https://picsum.photos/200/300",
            spotifyUrl = "spotify:track:3",
            previewUrl = null
        )
    )

    // 2. 지도에 뿌릴 핀 리스트
    fun getNearbyPins(): List<MapPin> = listOf(
        MapPin(
            soundmarkId = "pin_1",
            track = mockTracks[0],
            latitude = 37.5665,
            longitude = 126.9780,
            isActive = true,
            count = 2341
        ),
        MapPin(
            soundmarkId = "pin_2",
            track = mockTracks[1],
            latitude = 37.5675,
            longitude = 126.9800,
            isActive = true,
            count = 2341
        ),
        MapPin(
            soundmarkId = "pin_3",
            track = mockTracks[2],
            latitude = 37.5650,
            longitude = 126.9750,
            isActive = false, // 멀리 있는 경우 가정,
            count = 2341
        )
    )

    // 3. 상세 화면용 SoundMark 정보
    fun getSoundMarkDetail(id: String): SoundMark {
        return SoundMark(
            id = id,
            track = mockTracks[0],
            author = User.Default.copy(name = "음악대장"),
            location = GeoLocation(37.5665, 126.9780, "엘리스랩 성수"),
            message = "여기서 이 노래 들으면 감성이 폭발합니다.. 꼭 들어보세요!" + id,
            imageUrls = listOf("https://picsum.photos/200/300"),
            reactions = listOf(
                Reaction(ReactionType("fire", "🔥", "Fire"), 1234, true),
                Reaction(ReactionType("sad", "😢", "Sad"), 56, false),
                Reaction(ReactionType("love", "❤️", "Love"), 890, false),
                Reaction(ReactionType("party", "🥳", "Party"), 12, false), // 새로 추가된 리액션!
                Reaction(ReactionType("cool", "😎", "Cool"), 7, false)    // 코드 수정 없이 추가 가능
            ),
            createdAt = System.currentTimeMillis(),
            isActive = true
        )
    }
}