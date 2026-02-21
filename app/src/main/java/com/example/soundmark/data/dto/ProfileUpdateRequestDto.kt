package com.example.soundmark.data.dto

import com.google.gson.annotations.SerializedName

data class ProfileUpdateRequestDto(
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("profile_image") val profileImage: Int? = null,
    @SerializedName("status_message") val statusMessage: String? = null
)