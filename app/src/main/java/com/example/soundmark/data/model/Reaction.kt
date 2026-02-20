package com.example.soundmark.data.model

data class Reaction(
    val type: ReactionType,
    val count: Int,
    val reacted: Boolean
) {
    companion object {
        val Default = Reaction(
            type = ReactionType.LIKE,
            count = 0,
            reacted = false
        )
    }
}

enum class ReactionType {
    LIKE,
    LOVE,
    SAD,
    COOL
}