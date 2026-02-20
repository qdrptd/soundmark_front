package com.example.soundmark.data.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    val id: String,
    @SerializedName("display_name") val displayName: String?,
    val email: String?,
    val images: List<UserImage>?,
    val product: String?, // premium, free, etc.
    val type: String?,
    val uri: String?
)

data class UserImage(
    val url: String,
    val height: Int?,
    val width: Int?
)
