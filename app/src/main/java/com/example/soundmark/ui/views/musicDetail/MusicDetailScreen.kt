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

    Column(
        modifier = Modifier
            .padding(20.dp) // 팝업 내부 전체 여백
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. [수정됨] 상단 바 섹션: 장소/추천인 정보와 X 버튼을 한 줄에 배치
        TopBarSection(
            locationName = soundMark.location.placeName,
            authorName = soundMark.author.name,
            onBackClick = onBackClick,
            onProfileClick = { onProfileClick(soundMark.author.id) }
        )

        Spacer(Modifier.height(16.dp))

        // 2. 앨범 커버 (스케치처럼 큼직하게)
        AlbumCoverSection(track = soundMark.track)

        Spacer(Modifier.height(20.dp))

        // 3. 곡 정보 (제목, 아티스트)
        TrackInfoSection(track = soundMark.track)

        Spacer(Modifier.height(16.dp))

        // 4. 중앙 리액션 통계 (🔥 1,234)
        CentralReactionStats(count = 1234, icon = "🔥")

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), thickness = 2.dp)

        Spacer(Modifier.height(20.dp))

        // 5. 메시지 섹션
        MessageSection(message = soundMark.message)

        Spacer(Modifier.height(24.dp))

        // 6. Spotify 버튼
        SpotifyButton(
            spotifyUrl = soundMark.track.spotifyUrl,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(soundMark.track.spotifyUrl))
                context.startActivity(intent)
            }
        )

        Spacer(Modifier.height(24.dp))

        // 7. 하단 리액션 바 (others 및 이모지들)
        BottomReactionBar(
            reactions = soundMark.reactions,
            onReactionClick = { type -> /* TODO: API 호출 */ }
        )
    }
}

/** [신규 소부품] 상단 바: 위치/추천인 + X 버튼 */
@Composable
private fun TopBarSection(
    locationName: String?,
    authorName: String,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 왼쪽: 장소 및 추천인 정보
        Column(
            modifier = Modifier
                .weight(1f) // 남은 공간을 다 차지하여 X 버튼을 우측 끝으로 밉니다.
                .clickable(onClick = onProfileClick)
        ) {
            Text(
                text = "📍 ${locationName ?: "알 수 없는 장소"}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "by $authorName",
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            )
        }

        // 오른쪽: X 버튼 (기존 CloseButton 재사용)
        CloseButton(onClick = onBackClick)
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

/** * [공용 부품] 독립된 X 버튼
 * 이제 이 버튼은 상단 바뿐만 아니라 어디서든 호출할 수 있습니다.
 */
@Composable
private fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
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