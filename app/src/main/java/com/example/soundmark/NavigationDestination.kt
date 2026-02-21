package com.example.soundmark

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationDestination(
    val label: String,
    val icon: ImageVector? = null,
    @DrawableRes val iconResId: Int? = null,
    val showInBottomBar: Boolean = false,
    private val definedRoute: String? = null
) {
    HOME("홈", iconResId = R.drawable.ic_home, showInBottomBar = true),
    SONG_LIST("추천", iconResId = R.drawable.ic_feed, showInBottomBar = true),
    SONG_DETAIL("Song Detail", null, null, false, "SONG_DETAIL/{soundMarkId}"),
    SONG_ADD("Song Add"),
    PROFILE("마이페이지", iconResId = R.drawable.ic_person, showInBottomBar = true, definedRoute = "PROFILE/{userId}"),
    ONBOARD("Onboard"),
    PROFILE_EDIT("프로필 편집", null, null, false, "profile_edit?name={name}&message={message}&imageId={imageId}"),
    SPLASH("Splash");
    val route: String
        get() = definedRoute ?: this.name
}
