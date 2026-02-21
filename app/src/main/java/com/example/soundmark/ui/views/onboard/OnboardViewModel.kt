package com.example.soundmark.ui.views.onboard

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.BuildConfig
import com.example.soundmark.data.network.SpotifyAuthDataSource
import com.example.soundmark.data.repository.user.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(
    private val spotifyAuthDataSource: SpotifyAuthDataSource,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    private val redirectUri = "soundmark://callback"

    init {
        authRepository.isLoggedIn
            .onEach { loggedIn ->
                if (loggedIn) {
                    val token = authRepository.getAccessToken() ?: ""
                    _loginState.value = LoginState.Success(token)
                } else {
                    _loginState.value = LoginState.Idle
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 🔥 Spotify 로그인 버튼 클릭
     */
    fun onSpotifyLoginClick(context: Context) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                val (loginUri, verifier) = spotifyAuthDataSource.buildLoginUri(clientId, redirectUri)
                Log.d("verifier", verifier.toString())
                authRepository.saveCodeVerifier(verifier)
                CustomTabsIntent.Builder()
                    .build()
                    .launchUrl(context, loginUri)

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Login failed")
            }
        }
    }

    /**
     * 🔥 Redirect 처리
     */
    fun handleAuthRedirect(uri: Uri?) {
        if (uri == null || !uri.toString().startsWith(redirectUri)) return

        val authCode = uri.getQueryParameter("code")

        if (authCode != null) {
            loginWithSpotify(authCode)
        } else {
            val error = uri.getQueryParameter("error") ?: "Unknown error"
            _loginState.value = LoginState.Error(error)
        }
    }

    /**
     * 🔥 OAuth 전체 flow 실행
     */
    private fun loginWithSpotify(code: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            authRepository.handleSpotifyCallback(code)
                .onSuccess { token ->
                    _loginState.value = LoginState.Success(token)
                }
                .onFailure { e ->
                    _loginState.value = LoginState.Error(e.message ?: "Login failed")
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
