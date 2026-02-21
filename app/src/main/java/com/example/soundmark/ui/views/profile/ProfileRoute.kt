package com.example.soundmark.ui.views.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.soundmark.data.model.SoundMark

@Composable
fun ProfileRoute(userId: String) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Profile") }
            )
        }
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
                    contentPadding = PaddingValues(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        // Profile Header
                        Text(
                            text = profile.user.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(label = "SoundMarks", count = profile.user.soundMarkCount)
                            ProfileStatItem(label = "Followers", count = profile.user.followerCount)
                            ProfileStatItem(label = "Following", count = profile.user.followingCount)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Divider()
                        
                        Text(
                            text = "My SoundMarks",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        )
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
                    .then(if (isMe) Modifier else Modifier.blur(10.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title & Artist with optional Blur
                Text(
                    text = soundMark.track.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = if (isMe) Modifier else Modifier.blur(8.dp)
                )
                Text(
                    text = soundMark.track.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = if (isMe) Modifier else Modifier.blur(6.dp)
                )
                
                // Location & Message (Always visible)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📍 ${soundMark.location.placeName ?: "Unknown Location"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
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
        Text(text = count.toString(), style = MaterialTheme.typography.titleLarge)
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
