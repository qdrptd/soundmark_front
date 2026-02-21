package com.example.soundmark


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationDestination(
    val label: String,
    val icon: ImageVector? = null,
    val showInBottomBar: Boolean = false,
    private val definedRoute: String? = null
) {
    HOME("홈", Icons.Outlined.Person, true),
    SONG_LIST("피드", Icons.Outlined.Home, true),
    SONG_DETAIL("Song Detail", null, false, "SONG_DETAIL/{soundMarkId}"),
    SONG_ADD("Song Add"),
    PROFILE("마이페이지", Icons.Outlined.Person, true, "PROFILE/{userId}"),
    ONBOARD("Onboard");

    val route: String
        get() = definedRoute ?: this.name
}