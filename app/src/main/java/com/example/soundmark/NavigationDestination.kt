package com.example.soundmark


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationDestination(
    val label: String,
    val icon: ImageVector? = null,
    val showInBottomBar: Boolean = false,
    private val definedRoute: String? = null
) {
    HOME("Home", Icons.Default.Home, true),
    SONG_LIST("Song List", Icons.Default.List, true),
    SONG_DETAIL("Song Detail", null, false, "SONG_DETAIL/{soundMarkId}"),
    SONG_ADD("Song Add"),
    PROFILE("Profile", Icons.Default.AccountCircle, true, "PROFILE/{userId}"),
    ONBOARD("Onboard");

    val route: String
        get() = definedRoute ?: this.name
}