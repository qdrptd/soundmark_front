package com.example.soundmark.data.repository.user

interface AuthRepository {
    fun saveAccessToken(token: String)
    fun getAccessToken(): String?
    fun clearSession()
}
