package com.example.soundmark.data.model

data class Reaction(
    val type: ReactionType,
    val count: Int,
    val isReactedByMe: Boolean
)

/**
 * 리액션의 '정의' 자체를 나타냅니다.
 * 서버에서 관리하며, 새로운 리액션이 추가되어도 클라이언트 코드는 변하지 않습니다.
 */
data class ReactionType(
    val id: String,       // 고유 식별자 (예: "fire", "heart_eyes")
    val emoji: String,    // 표시할 이모지 (예: "🔥")
    val displayName: String // 접근성이나 라벨용 이름 (예: "불")
) {
    companion object {
        // 기본적으로 제공할 리액션들 (초기값 또는 폰백용)
        val DEFAULT_TYPES = listOf(
            ReactionType("fire", "🔥", "Fire"),
            ReactionType("sad", "😢", "Sad"),
            ReactionType("love", "❤️", "Love"),
            ReactionType("clap", "👏", "Clap")
        )

        val OTHERS = ReactionType("others", "➕", "Others")
    }
}