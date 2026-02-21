package com.example.soundmark.ui.views.musicDetail

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@Composable
fun MusicDetailScreen(
    soundMarkId: String,
    viewModel: MusicDetailViewModel,
    onBackClick: () -> Unit,
    onProfileClick: (userId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Log.d("MusicDetailScreen", "soundMarkId: $soundMarkId")

    LaunchedEffect(soundMarkId) {
        viewModel.loadSoundMark(soundMarkId)
    }

    // [핵심] 전체 화면을 차지하는 Box를 만들되 배경은 투명하게 유지합니다.
    // 클릭 시 닫히는 기능을 넣어 팝업 바깥을 누르면 꺼지게 합니다.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onBackClick() },
        contentAlignment = Alignment.Center // 중앙에 팝업 배치
    ) {
        // 실제 팝업 카드 영역
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f) // 가로 85%만 사용 (사방 여백 발생)
                .wrapContentHeight() // 내용만큼만 높이 조절
                .clickable(enabled = false) { }, // 카드 내부 클릭은 무시
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                // --- 우상단 X 버튼 ---
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(18.dp)
                    )
                }

                // --- 컨텐츠 영역 ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    when (val state = uiState) {
                        is MusicDetailUiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.padding(40.dp))
                        }
                        is MusicDetailUiState.Error -> {
                            Text(state.message, modifier = Modifier.padding(40.dp))
                        }
                        is MusicDetailUiState.Success -> {
                            val mark = state.soundMark
                            val track = mark.track
                            val author = mark.author

                            // 1. 앨범 이미지 (둥근 모서리 적용)
                            AsyncImage(
                                model = track.albumCoverUrl,
                                contentDescription = "Album Art",
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Spacer(Modifier.height(16.dp))

                            // 2. 곡 정보
                            Text(track.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(track.artist, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                            Spacer(Modifier.height(20.dp))

                            // 3. 메시지 카드
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("📍 ${mark.location.placeName ?: "알 수 없는 장소"}", style = MaterialTheme.typography.labelSmall)
                                    TextButton(
                                        onClick = { onProfileClick(author.id) },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("${author.name} 님의 추천 >", style = MaterialTheme.typography.labelMedium)
                                    }
                                    mark.message?.let {
                                        Text(it, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // 4. Spotify 버튼
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(track.spotifyUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Spotify에서 재생")
                            }
                        }
                    }
                }
            }
        }
    }
}