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
    val id: String,       // 이제 이모지 유니코드나 문자열 자체를 ID로 씁니다. (예: "🔥")
    val emoji: String,    // 표시할 문자열
    val displayName: String
) {
    companion object {
        // 사용자가 이모지 키보드에서 아무거나 골랐을 때 객체를 생성해주는 헬퍼 함수
        fun fromEmoji(emoji: String): ReactionType {
            return ReactionType(
                id = emoji,
                emoji = emoji,
                displayName = "Emoji"
            )
        }

        val OTHERS = ReactionType("others", "➕", "Others")
    }
}