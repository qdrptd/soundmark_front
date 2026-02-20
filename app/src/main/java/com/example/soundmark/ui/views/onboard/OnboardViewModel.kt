package com.example.soundmark.ui.views.onboard

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import com.example.soundmark.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    init {
        // 이미 저장된 토큰이 있는지 확인하여 자동 로그인 상태 설정
        userRepository.getAccessToken()?.let {
            _loginState.value = LoginState.Success(it)
        }
    }

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
            .appendQueryParameter("response_type", "token")
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
                userRepository.saveAccessToken(accessToken)
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
