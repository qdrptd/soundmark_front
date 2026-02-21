package com.example.soundmark.data.model

data class Reaction(
    val type: ReactionType,
    val count: Int,
    val reacted: Boolean
) {
    companion object {
        val Default = Reaction(
            type = ReactionType.FIRE,
            count = 0,
            reacted = false
        )
    }
}

enum class ReactionType(val emoji: String, val displayName: String) {
    FIRE("🔥", "Fire"),
    SAD("😢", "Sad"),
    LOVE("❤️", "Love"),
    CLAP("👏", "Clap"),
    OTHERS("➕", "Others") // 스케치에 있던 'others' 버튼용
}