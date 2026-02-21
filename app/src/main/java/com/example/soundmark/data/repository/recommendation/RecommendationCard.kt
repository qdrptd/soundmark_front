package com.example.soundmark.data.repository.recommendation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.soundmark.data.model.PlaceRecommendationCard

@Composable
fun RecommendationCard(
    cardData: PlaceRecommendationCard,
    modifier: Modifier = Modifier
) {
    val neonGreen = Color(0xFF1DB954) // 스포티파이/네온 그린 컬러

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF121212)) // 다크 배경
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            // 왼쪽: 레이더 효과 + 앨범 아트
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                // 배경 동심원 효과 (레이더)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = Color.White.copy(alpha = 0.05f), radius = size.minDimension / 2, style = Stroke(1.dp.toPx()))
                    drawCircle(color = Color.White.copy(alpha = 0.1f), radius = size.minDimension / 3, style = Stroke(1.dp.toPx()))
                }

                AsyncImage(
                    model = cardData.albumCoverUrl,
                    contentDescription = "Album Cover",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, neonGreen, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 오른쪽: 텍스트 정보
            Column {
                Text(
                    text = "내가 요즘 자주 듣는",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                // 장르와 장소 강조 텍스트
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = neonGreen, fontWeight = FontWeight.Bold, fontSize = 24.sp)) {
                            append(cardData.matchedGenre)
                        }
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 18.sp)) {
                            append(" 장르는\n")
                        }
                        withStyle(style = SpanStyle(color = neonGreen, fontWeight = FontWeight.Bold, fontSize = 24.sp)) {
                            append(cardData.placeName)
                        }
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 18.sp)) {
                            append(" 에")
                        }
                    },
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 곡 개수 배지
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(neonGreen)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${cardData.totalRecommendations}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = " 곡 심어져있어요",
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}