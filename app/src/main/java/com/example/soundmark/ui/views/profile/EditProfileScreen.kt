package com.example.soundmark.ui.views.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
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
            Text("나의 성장 아이콘", style = MaterialTheme.typography.labelLarge)

            // 아이콘 선택 리스트 (1~5)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..5).forEach { id ->
                    val isSelected = selectedId == id
                    IconButton(
                        onClick = { viewModel.selectedImageId.value = id },
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                if (isSelected) PointGreen.copy(alpha = 0.2f) else Color.Transparent,
                                CircleShape
                            )
                            .border(
                                if (isSelected) 2.dp else 0.dp,
                                if (isSelected) PointGreen else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        // 아이콘 리소스 (id에 따라 대응)
                        Icon(
                            painter = painterResource(id = getIconResById(id)),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
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
    1 -> R.drawable.icon1
    2 -> R.drawable.icon2
    3 -> R.drawable.icon3
    4 -> R.drawable.icon4
    else -> R.drawable.icon5
}