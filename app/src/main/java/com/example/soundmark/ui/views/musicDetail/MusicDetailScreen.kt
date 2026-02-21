package com.example.soundmark.ui.views.musicDetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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

@Composable
fun MusicDetailScreen(
    soundMarkId: String,
    viewModel: MusicDetailViewModel,
    onBackClick: () -> Unit,
    onProfileClick: (userId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEmojiPicker by remember { mutableStateOf(false) }

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
                    onBackClick = onBackClick,
                    onOthersClick = { showEmojiPicker = true },
                    onReactionClick = { type -> viewModel.toggleReaction(type) }
                )
            }
        }
    }

    if (showEmojiPicker) {
        EmojiPickerDialog(
            onDismiss = { showEmojiPicker = false },
            onEmojiSelected = { emoji ->
                viewModel.toggleReaction(ReactionType.fromEmoji(emoji))
                showEmojiPicker = false
            }
        )
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
    onBackClick: () -> Unit,
    onOthersClick: () -> Unit,
    onReactionClick: (ReactionType) -> Unit
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
        TopReactionsSummary(reactions = soundMark.reactions)

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
            onOthersClick = onOthersClick,
            onReactionClick = onReactionClick
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
private fun TopReactionsSummary(reactions: List<Reaction>) {
    val top4Reactions = reactions
        .sortedByDescending { it.count }
        .filter { it.count > 0 }
        .take(4)

    val totalCount = reactions.sumOf { it.count }

    if (totalCount > 0) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 1. 겹쳐진 원형 이모지 스택
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                // 뒤에 있는 것부터 먼저 그려야 겹침이 자연스럽습니다.
                top4Reactions.forEachIndexed { index, reaction ->
                    ReactionCircle(
                        emoji = reaction.type.emoji,
                        modifier = Modifier.padding(start = (index * 20).dp) // 20dp씩 겹치게 설정
                    )
                }
            }

            // 2. 이모지 개수만큼 간격 확보를 위한 계산된 여백
            val extraPadding = if (top4Reactions.size > 1) (top4Reactions.size * 5).dp else 0.dp
            Spacer(modifier = Modifier.width(extraPadding))

            // 3. 총 숫자 표시
            Text(
                text = String.format("%,d", totalCount),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            )
        }
    }
}

/** [신규 부품] 이모지를 담는 깔끔한 원형 컨테이너 */
@Composable
private fun ReactionCircle(
    emoji: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(30.dp), // 원의 크기
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface, // 원 내부 배경색
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant), // 테두리
        shadowElevation = 2.dp // 살짝 입체감 추가
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
        }
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
/** [소부품 F] 하단 리액션 바
 * 이제 고정된 Enum이 아니라 서버나 Mock에서 내려주는 reactions 리스트를 그대로 그립니다.
 */
@Composable
private fun BottomReactionBar(
    reactions: List<Reaction>, // [🔥 10개, ❤️ 5개, 👏 2개 ...] 형태로 들어옴
    onOthersClick: () -> Unit,
    onReactionClick: (ReactionType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Others 버튼 (항상 맨 앞에 배치하거나 스케치 위치에 고정)
        ReactionButton(
            icon = ReactionType.OTHERS.emoji,
            label = "others",
            isSelected = false,
            onClick = onOthersClick
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 2. 동적 리액션 리스트
        // 데이터가 10개든 20개든 가로로 쭉 나열됩니다.
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(reactions) { reaction ->
                ReactionButton(
                    icon = reaction.type.emoji,
                    label = if (reaction.count > 0) "${reaction.count}" else "",
                    isSelected = reaction.isReactedByMe,
                    onClick = { onReactionClick(reaction.type) }
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