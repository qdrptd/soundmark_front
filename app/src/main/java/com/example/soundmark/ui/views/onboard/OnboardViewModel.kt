package com.example.soundmark.ui.views.onboard

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow



class OnboardViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    // Replace with your actual client ID and redirect URI from Spotify Developer Dashboard
    private val clientId = "YOUR_CLIENT_ID"
    private val redirectUri = "soundmark://callback"
    private val scopes = "user-read-private user-read-email"

    fun onSpotifyLoginClick(context: Context) {
        _loginState.value = LoginState.Loading
        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("accounts.spotify.com")
            .appendPath("authorize")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("response_type", "token") // or "code" for Authorization Code Flow
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("scope", scopes)
            .build()

        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, authUrl)
    }

    fun handleAuthRedirect(uri: Uri?) {
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            val accessToken = uri.fragment?.split("&")
                ?.find { it.startsWith("access_token=") }
                ?.substringAfter("access_token=")

            if (accessToken != null) {
                _loginState.value = LoginState.Success(accessToken)
            } else {
                val error = uri.getQueryParameter("error") ?: "Unknown error"
                _loginState.value = LoginState.Error(error)
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val accessToken: String) : LoginState()
    data class Error(val message: String) : LoginState()
}