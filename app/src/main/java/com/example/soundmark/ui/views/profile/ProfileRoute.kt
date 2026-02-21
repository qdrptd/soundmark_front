package com.example.soundmark.ui.views.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.soundmark.util.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.soundmark.data.model.SoundMark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(userId: String) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            }
        } else {
            uiState.profile?.let { profile ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        // Profile Information Box
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(25.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(0.7.dp, MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(25.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Profile Image
                                AsyncImage(
                                    model = profile.user.profileImageUrl ?: "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = profile.user.name,
                                        style = MaterialTheme.typography.titleMedium.bold().size(20),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "@${profile.user.name.lowercase()}",
                                        style = MaterialTheme.typography.bodySmall.size(14).color(MaterialTheme.colorScheme.outline),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "음악과 함께 걷는 여행자 ",
                                        style = MaterialTheme.typography.bodySmall.size(14),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                                    ) {
                                        ProfileStatItem(label = "그루", count = profile.user.soundMarkCount)
                                        ProfileStatItem(label = "팔로워", count = profile.user.followerCount)
                                        ProfileStatItem(label = "팔로잉", count = profile.user.followingCount)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "More"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "내가 최근에 심은 그루",
                            style = MaterialTheme.typography.titleMedium.size(18),
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (profile.mySoundMarks.isEmpty()) {
                        item {
                            EmptySoundMarkView()
                        }
                    } else {
                        val displayList = if (isExpanded) {
                            profile.mySoundMarks
                        } else {
                            profile.mySoundMarks.take(3)
                        }

                        items(displayList) { soundMark ->
                            SoundMarkItem(soundMark = soundMark, isMe = uiState.isMe)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (!isExpanded && profile.mySoundMarks.size > 3) {
                            item {
                                TextButton(
                                    onClick = { isExpanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("더보기 (${profile.mySoundMarks.size - 3}개 더 있음)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptySoundMarkView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "등록된 사운드마크가 없습니다.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "주변의 멋진 장소에 음악을 남겨보세요!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}


@Composable
fun SoundMarkItem(soundMark: SoundMark, isMe: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Cover with optional Blur
            AsyncImage(
                model = soundMark.track.albumCoverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .blurIf(!isMe, 10.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title & Artist with optional Blur
                Text(
                    text = soundMark.track.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.blurIf(!isMe, 8.dp)
                )
                Text(
                    text = soundMark.track.artist,
                    style = MaterialTheme.typography.bodySmall.color(MaterialTheme.colorScheme.outline),
                    modifier = Modifier.blurIf(!isMe, 6.dp)
                )
                
                // Location & Message (Always visible)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📍 ${soundMark.location.placeName ?: "Unknown Location"}",
                    style = MaterialTheme.typography.labelSmall.color(MaterialTheme.colorScheme.outline)
                )
                if (!soundMark.message.isNullOrBlank()) {
                    Text(
                        text = soundMark.message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(modifier = Modifier.padding(4.dp), text = count.toString(), style = MaterialTheme.typography.titleSmall.bold().size(16))
        Text(text = label, style = MaterialTheme.typography.bodyMedium.size(14))
    }
}
