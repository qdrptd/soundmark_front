package com.example.soundmark.ui.views.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.soundmark.data.model.ClusterMark
import com.example.soundmark.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSongDetail: (String) -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val clusters by viewModel.clusteredMarks.collectAsState()
    val selectedCluster by viewModel.selectedCluster.collectAsState()
    val context = LocalContext.current

    // 기본 설정
    val sheetState = rememberModalBottomSheetState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 15f)
    }
    val uiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        myLocationButtonEnabled = true
    )
    val properties = MapProperties(isMyLocationEnabled = true)

    // 카메라의 줌 레벨이 변하는 것을 관찰하여 ViewModel에 전달
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            viewModel.onZoomChanged(cameraPositionState.position.zoom)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = ComposeColor.White,
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add SoundMark")
            }
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier.padding(padding).fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings
        ) {
            clusters.forEach { cluster ->
                key("${cluster.id}_${cluster.isActive}") {
                    Marker(
                        state = rememberMarkerState(position = cluster.position),
                        // 여기서 다시 숫자가 들어간 아이콘을 만듭니다!
                        icon = createClusterIcon(context, cluster.count, cluster.isActive),
                        onClick = {
                            viewModel.onClusterClick(cluster)
                            true
                        }
                    )
                }
            }
        }

        // ClusterMark를 눌렀을 때 나오는 BottomSheet
        if (selectedCluster != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissBottomSheet() },
                sheetState = sheetState
            ) {
                ClusterDetailContent(cluster = selectedCluster!!,
                    onItemClick = { soundMarkId ->
                        viewModel.dismissBottomSheet() // 시트 먼저 닫기
                        onNavigateToSongDetail(soundMarkId) // 상세 화면으로 이동
                    })
            }
        }
    }
}

@Composable
fun ClusterDetailContent(
    cluster: ClusterMark,
    onItemClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        if (cluster.isActive) {
            // 활성화 상태: 노래 목록 표시
            Text(
                text = "이 구역의 사운드 마크 (${cluster.count}개)",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(androidx.compose.ui.Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            cluster.pins.forEach { pin ->
                ListItem(
                    modifier = Modifier.clickable { onItemClick(pin.soundmarkId) },
                    headlineContent = { Text(pin.track.title) },
                    supportingContent = { Text(pin.track.artist) },
                    leadingContent = {
                        AsyncImage(
                            model = pin.track.albumCoverUrl,
                            contentDescription = "Album Cover",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                )
            }
        } else {
            // 비활성화 상태: 접근 제한 안내 문구 표시
            Spacer(modifier = Modifier.height(32.dp))
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(48.dp),
                tint = ComposeColor.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "조금 더 가까이 다가가 보세요!",
                style = MaterialTheme.typography.bodyLarge,
                color = ComposeColor.Gray
            )
            Text(
                text = "반경 내에 들어와야 노래 정보를 확인할 수 있습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = ComposeColor.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

fun createClusterIcon(
    context: Context,
    count: Int,
    isActive: Boolean
): BitmapDescriptor {
    // 1. 기본 아이콘 이미지 로드 (icon1)
    val baseBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon1)

    // 원본 이미지 크기 조절 (필요시)
    val iconSize = 120
    val scaledIcon = Bitmap.createScaledBitmap(baseBitmap, iconSize, iconSize, false)

    // 최종 비트맵 크기 결정 (뱃지가 아래에 추가될 공간 확보)
    val width = iconSize
    val height = if (count > 1) iconSize + 60 else iconSize
    val finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(finalBitmap)

    // 2. 이미지 Paint 설정 (비활성 상태일 때 투명도나 회색 필터 적용)
    val iconPaint = Paint().apply {
        if (!isActive) {
            val colorMatrix = ColorMatrix().apply { setSaturation(0f) } // 흑백 처리
            colorFilter = ColorMatrixColorFilter(colorMatrix)
            alpha = 150 // 반투명
        }
    }

    // 중심 정렬을 위해 x좌표 계산
    val centerX = width / 2f
    canvas.drawBitmap(scaledIcon, centerX - (iconSize / 2f), 0f, iconPaint)

    // 3. Count 조건에 따른 Rect 및 텍스트 그리기
    if (count > 1) {
        val displayText = when {
            count >= 50 -> "50+"
            count >= 10 -> "10+"
            else -> count.toString()
        }

        val rectPaint = Paint().apply {
            color = if (isActive) Color.parseColor("#6200EE") else Color.LTGRAY
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        // 텍스트 크기 측정 및 Rect 영역 계산
        val textBounds = Rect()
        textPaint.getTextBounds(displayText, 0, displayText.length, textBounds)
        val rectWidth = textBounds.width() + 30f
        val rectHeight = textBounds.height() + 20f

        val rectLeft = centerX - (rectWidth / 2f)
        val rectTop = iconSize.toFloat() - 10f // 아이콘과 살짝 겹치게 배치
        val rectRight = rectLeft + rectWidth
        val rectBottom = rectTop + rectHeight

        // 둥근 사각형 뱃지 그리기
        canvas.drawRoundRect(
            rectLeft, rectTop, rectRight, rectBottom,
            15f, 15f, rectPaint
        )

        // 텍스트 그리기
        val textY = rectTop + (rectHeight / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(displayText, centerX, textY, textPaint)
    }

    return BitmapDescriptorFactory.fromBitmap(finalBitmap)
}