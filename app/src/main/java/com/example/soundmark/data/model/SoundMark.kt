package com.example.soundmark.data.model

data class SoundMark(
    val id: String,
    val track: Track,
    val author: User,
    val location: GeoLocation,
    val message: String?,
    val imageUrls: List<String>,
    val reactions: List<Reaction>,
    val createdAt: Long,
    val isActive: Boolean
) {
    companion object {
        val Default = SoundMark(
            id = "1234",
            track = Track.Default,
            author = User.Default,
            location = GeoLocation.Default,
            message = "SoundMark message",
            imageUrls = emptyList(),
            reactions = emptyList(),
            createdAt = System.currentTimeMillis(),
            isActive = true
        )
    }
}
