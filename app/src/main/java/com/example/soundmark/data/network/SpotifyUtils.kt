package com.example.soundmark.data.network

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.browser.customtabs.CustomTabsIntent
import com.example.soundmark.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton
import java.security.MessageDigest
import java.util.UUID


class SpotifyAuthDataSource @Inject constructor() {

    fun buildLoginUri(clientId: String, redirectUri: String): Pair<Uri, String> {
        val codeVerifier = generateCodeVerifier()
        val challenge = generateCodeChallenge(codeVerifier)

        return Pair(Uri.Builder()
            .scheme("https")
            .authority("accounts.spotify.com")
            .appendPath("authorize")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("scope", "user-read-private user-read-email")
            .build(), codeVerifier)
    }


    private fun generateCodeVerifier(): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
        return (1..64)
            .map { charset.random() }
            .joinToString("")
    }

    private fun generateCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.UTF_8)

        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return Base64.encodeToString(digest, Base64.NO_WRAP or Base64.URL_SAFE)
            .replace("=", "")
    }
}
