package com.example.soundmark.ui.views.recommendation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soundmark.data.repository.recommendation.RecommendationCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendationScreen(
    viewModel: RecommendationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 40.dp)
    ) {
        // 타이틀 영역
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "내가 최근 빠진 장르",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "음악과 장소가 만나는 나만의 취향 지도",
                color = Color(0xFF1DB954), // 포인트 그린
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 상태별 UI 분기
        when (val state = uiState) {
            is RecommendationUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1DB954))
                }
            }
            is RecommendationUiState.Success -> {
                val pagerState = rememberPagerState(pageCount = { state.cards.size })

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        pageSpacing = 12.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        RecommendationCard(cardData = state.cards[page])
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 페이지 인디케이터 (간단 구현)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(state.cards.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color(0xFF1DB954) else Color.DarkGray
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                                    .size(width = if (pagerState.currentPage == iteration) 24.dp else 8.dp, height = 8.dp)
                            )
                        }
                    }
                }
            }
            is RecommendationUiState.Error -> {
                Text(
                    text = state.message ?: "데이터를 불러오지 못했습니다.",
                    color = Color.Red,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
    }
}