package com.example.soundmark.ui.views.musicDetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.soundmark.data.model.Reaction
import com.example.soundmark.data.model.ReactionType
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.model.Track

// 1. 사용할 리액션 타입 정의 (데이터 모델에 정의되어 있다고 가정)
val availableReactionTypes = listOf(
    ReactionType.FIRE,
    ReactionType.SAD,
    ReactionType.LOVE,
    ReactionType.CLAP
)

@Composable
fun MusicDetailScreen(
    soundMarkId: String,
    viewModel: MusicDetailViewModel,
    onBackClick: () -> Unit,
    onProfileClick: (userId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(soundMarkId) {
        viewModel.loadSoundMark(soundMarkId)
    }

    // 팝업 컨테이너는 그대로 유지
    DetailPopupWrapper(onBackClick = onBackClick) {
        when (val state = uiState) {
            is MusicDetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is MusicDetailUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is MusicDetailUiState.Success -> {
                MusicDetailContent(
                    soundMark = state.soundMark,
                    onProfileClick = onProfileClick,
                    onBackClick = onBackClick
                )
            }
        }
    }
}

/** [부품 1] 팝업의 틀 (기존과 동일) */
@Composable
private fun DetailPopupWrapper(
    onBackClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onBackClick() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f) // 너비를 조금 더 넓게
                .wrapContentHeight()
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            content()
        }
    }
}

/** [부품 2] 성공 시 보여줄 메인 컨텐츠 조합 (전면 수정) */
@Composable
private fun MusicDetailContent(
    soundMark: SoundMark,
    onProfileClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Box {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. 앨범 커버 (가장 크게 배치)
            AlbumCoverSection(track = soundMark.track)

            Spacer(Modifier.height(20.dp))

            // 2. 곡 정보 (제목, 아티스트)
            TrackInfoSection(track = soundMark.track)

            Spacer(Modifier.height(16.dp))

            // 3. 중앙 리액션 통계 (예: 총 좋아요 수)
            // 임시로 🔥 아이콘과 1,234라는 숫자를 사용합니다.
            CentralReactionStats(count = 1234, icon = "🔥")

            Spacer(Modifier.height(20.dp))

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            Spacer(Modifier.height(20.dp))

            // 4. 메시지 섹션 (텍스트만 깔끔하게)
            MessageSection(message = soundMark.message)

            Spacer(Modifier.height(24.dp))

            // 5. Spotify 버튼
            SpotifyButton(
                spotifyUrl = soundMark.track.spotifyUrl,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(soundMark.track.spotifyUrl))
                    context.startActivity(intent)
                }
            )

            Spacer(Modifier.height(24.dp))

            // 6. 하단 리액션 버튼 바 (others)
            BottomReactionBar(
                    reactions = soundMark.reactions, // 실제 데이터 연결 필요
            onReactionClick = { type -> /* TODO: 리액션 API 호출 */ }
            )
        }

        // 7. 상단 플로팅 요소들 (장소 정보, X 버튼)
        TopFloatingElements(
            locationName = soundMark.location.placeName,
            authorName = soundMark.author.name,
            onBackClick = onBackClick,
            onProfileClick = { onProfileClick(soundMark.author.id) }
        )
    }
}

/** [소부품 A] 앨범 커버 섹션 (크게) */
@Composable
private fun AlbumCoverSection(track: Track) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier.size(260.dp) // 크기 대폭 확대
    ) {
        AsyncImage(
            model = track.albumCoverUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/** [소부품 B] 곡 정보 섹션 (제목 강조) */
@Composable
private fun TrackInfoSection(track: Track) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = track.title,
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = track.artist,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        )
    }
}

/** [소부품 C] 중앙 리액션 통계 */
@Composable
private fun CentralReactionStats(count: Int, icon: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = String.format("%,d", count), // 1,234 형태로 포맷팅
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
    }
}

/** [소부품 D] 메시지 섹션 (깔끔한 텍스트) */
@Composable
private fun MessageSection(message: String?) {
    message?.let {
        Text(
            text = it,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp, // 줄 간격 조정
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** [소부품 E] Spotify 버튼 (둥글게, 아이콘 포함) */
@Composable
private fun SpotifyButton(spotifyUrl: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // 높이 설정
        shape = RoundedCornerShape(28.dp), // 완전히 둥글게
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1DB954), // Spotify 브랜드 컬러 (선택사항)
            contentColor = Color.White
        )
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Spotify에서 듣기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BottomReactionBar(
    reactions: List<Reaction>,
    onReactionClick: (ReactionType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Others 버튼 (스케치 좌측 하단)
        ReactionButton(
            icon = ReactionType.OTHERS.emoji,
            label = "others",
            isSelected = false,
            onClick = { onReactionClick(ReactionType.OTHERS) }
        )

        // 2. 나머지 주요 리액션들
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(availableReactionTypes) { type ->
                ReactionButton(
                    icon = type.emoji,
                    label = "", // 개별 버튼에는 레이블 생략 가능
                    isSelected = reactions.any { it.type == type && it.reacted },
                    onClick = { onReactionClick(type) }
                )
            }
        }
    }
}

/** [소부품 F-1] 원형 리액션 버튼 */
@Composable
private fun ReactionButton(
    icon: String,
    label: String = "",
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp) // 스케치처럼 큼직하게
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 28.sp)
        }
        if (label.isNotEmpty()) {
            Text(text = label, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
        }
    }
}

/** [소부품 G] 상단 플로팅 요소 (장소 정보, X 버튼) */
@Composable
private fun TopFloatingElements(
    locationName: String?,
    authorName: String,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 1. 좌측 상단: 장소 및 추천인 정보 (스케치의 나무 모양 위치)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .clickable(onClick = onProfileClick)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Column {
                Text(locationName ?: "알 수 없는 장소", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("by $authorName", fontSize = 10.sp, color = Color.Gray)
            }
        }

        // 2. 우측 상단: X 버튼
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
    }
}