package com.example.soundmark.data.model

data class Profile(
    val user: User,
    val mySoundMarks: List<SoundMark>,
    val likedSoundMarks: List<SoundMark>
) {
    companion object {
        val Default = Profile(
            user = User.Default,
            mySoundMarks = listOf(
                SoundMark.Default,
                SoundMark.Default,
                SoundMark.Default,
                SoundMark.Default
            ),
            likedSoundMarks = listOf(
                SoundMark.Default,
                SoundMark.Default,
                SoundMark.Default,
                SoundMark.Default
            ),
        )
    }
}
