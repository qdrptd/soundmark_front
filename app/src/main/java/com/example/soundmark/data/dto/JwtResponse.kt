package com.example.soundmark.data.dto

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String?,
    val userId: String
)