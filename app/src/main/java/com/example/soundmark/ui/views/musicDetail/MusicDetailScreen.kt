package com.example.soundmark.ui.views.musicDetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.soundmark.data.model.ReactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicDetailScreen(
    soundMarkId: String,
    viewModel: MusicDetailViewModel,
    onBackClick: () -> Unit,
    onProfileClick: (userId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(soundMarkId) {
        viewModel.loadSoundMark(soundMarkId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SoundMark 정보") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is MusicDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is MusicDetailUiState.Error -> Text(state.message, Modifier.align(Alignment.Center))
                is MusicDetailUiState.Success -> {
                    val mark = state.soundMark
                    val track = mark.track
                    val author = mark.author

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. 트랙 정보 (앨범 커버 + 제목 + 아티스트)
                        AsyncImage(
                            model = track.albumCoverUrl,
                            contentDescription = "Album Art",
                            modifier = Modifier.size(240.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(track.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(track.artist, style = MaterialTheme.typography.bodyLarge)

                        // 2. 장소 및 작성자 정보
                        Spacer(Modifier.height(24.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📍 ${mark.location.placeName ?: "알 수 없는 장소"}", style = MaterialTheme.typography.labelMedium)
                                Spacer(Modifier.height(8.dp))
                                TextButton(
                                    onClick = { onProfileClick(author.id) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("By ${author.name} (팔로워 ${author.followerCount}) >")
                                }
                                mark.message?.let {
                                    Text(it, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        // 3. 리액션 섹션
                        Spacer(Modifier.height(16.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(mark.reactions) { reaction ->
                                FilterChip(
                                    selected = reaction.reacted,
                                    onClick = { /* 반응 보내기 API 호출 */ },
                                    label = { Text("${reaction.type.name} ${reaction.count}") }
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        // 4. Spotify 연동 버튼
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(track.spotifyUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Spotify에서 재생하기")
                        }
                    }
                }
            }
        }
    }
}