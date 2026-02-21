package com.example.soundmark.ui.views.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soundmark.ui.theme.PointGreen
import com.example.soundmark.util.bold
import com.example.soundmark.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    initialName: String,
    initialMessage: String,
    initialImageId: Int,
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val name by viewModel.displayName.collectAsState()
    val message by viewModel.statusMessage.collectAsState()
    val selectedId by viewModel.selectedImageId.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setupData(initialName, initialMessage, initialImageId)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("프로필 수정", style = MaterialTheme.typography.titleMedium.bold()) },
                actions = {
                    TextButton(onClick = { viewModel.updateProfile() }) {
                        Text("완료", color = PointGreen, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("나의 아이콘", style = MaterialTheme.typography.labelLarge)

            // 아이콘 선택 리스트 (1~9, 3x3 그리드)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3열 고정
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp), // 3줄이 충분히 들어갈 높이
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(9) { index ->
                    val id = index + 1
                    val isSelected = selectedId == id

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f) // 정사각형 유지
                            .clip(CircleShape)
                            .background(
                                if (isSelected) PointGreen.copy(alpha = 0.2f) else Color.Transparent
                            )
                            .border(
                                if (isSelected) 2.dp else 0.dp,
                                if (isSelected) PointGreen else Color.Transparent,
                                CircleShape
                            )
                            .clickable { viewModel.selectedImageId.value = id },
                        contentAlignment = Alignment.Center
                    ) {
                        // 개별 아이콘 버튼
                        Icon(
                            painter = painterResource(id = getIconResById(id)),
                            contentDescription = "Icon $id",
                            tint = Color.Unspecified, // 원본 색상 유지
                            modifier = Modifier.size(64.dp) // 이미지 크기에 맞춰 조절
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.displayName.value = it },
                label = { Text("닉네임") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { viewModel.statusMessage.value = it },
                label = { Text("상태 한마디") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

// 아이콘 매핑 유틸
fun getIconResById(id: Int) = when(id) {
    1 -> R.drawable.icon_profile1
    2 -> R.drawable.icon_profile2
    3 -> R.drawable.icon_profile3
    4 -> R.drawable.icon_profile4
    5 -> R.drawable.icon_profile5
    6 -> R.drawable.icon_profile6
    7 -> R.drawable.icon_profile7
    8 -> R.drawable.icon_profile8
    else -> R.drawable.icon_profile4
}