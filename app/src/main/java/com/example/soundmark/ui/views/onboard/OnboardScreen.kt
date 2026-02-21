package com.example.soundmark.ui.views.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundmark.R
import com.example.soundmark.ui.theme.BackgroundDark
import com.example.soundmark.util.bold
import com.example.soundmark.util.color
import com.example.soundmark.util.size

@Composable
fun OnboardScreen(
    viewModel: OnboardViewModel = viewModel()
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_groo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.25f)   // 화면 가로의 1/4
                    .aspectRatio(77f / 137f),   // 정확한 비율
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))


        }
        when (loginState) {
            is LoginState.Loading -> {
                CircularProgressIndicator()
            }

            is LoginState.Error -> {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
                LoginButton(
                    onClick = { viewModel.onSpotifyLoginClick(context) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 45.dp)
                        .fillMaxWidth()
                )
            }

            else -> {
                LoginButton(
                    onClick = { viewModel.onSpotifyLoginClick(context) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 45.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun LoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                painter = painterResource(R.drawable.icon_spotify), // svg xml
                contentDescription = null,
                tint = Color.Unspecified, // 원본 색 유지 (중요)
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Spotify로 로그인하기",
                style = MaterialTheme.typography.bodyMedium.size(18).bold(),
                color = BackgroundDark
            )
        }
    }
}