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
import android.graphics.RectF
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
                contentColor = MaterialTheme.colorScheme.background,
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
                    supportingContent = { Text(pin.track.artist, color = MaterialTheme.colorScheme.outline) },
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

    // 1. count 수에 따라 사용할 이미지 리소스 결정
    val iconRes = when {
        count >= 50 -> R.drawable.icon_map50up
        count >= 10 -> R.drawable.icon_map10up
        count > 1    -> R.drawable.icon_map2up
        else         -> R.drawable.icon1       // FIXME
    }

    // 2. 결정된 리소스로 비트맵 로드
    val baseBitmap = BitmapFactory.decodeResource(context.resources, iconRes)
    val iconSize = 100
    val scaledIcon = Bitmap.createScaledBitmap(baseBitmap, iconSize, iconSize, false)

    // 여백 및 최종 비트맵 크기 계산
    val shadowRadius = 15f  // 그림자 번짐 정도
    val margin = 30f        // 그림자가 잘리지 않도록 여백 확보
    val badgeHeight = if (count > 1) 50f else 0f

    val width = (iconSize + margin * 2).toInt()
    val height = (iconSize + margin * 2 + badgeHeight).toInt()

    val finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(finalBitmap)

    val centerX = width / 2f
    val centerY = iconSize / 2f + margin

// 4. 그림자 레이어 그리기 (아이콘 아래에 입체감 부여)
    val shadowPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE // 그림자의 토대가 되는 배경색
        // setShadowLayer(반지름, x오프셋, y오프셋, 색상)
        if (isActive) {
            setShadowLayer(shadowRadius, 0f, 6f, Color.parseColor("#60000000"))
        }
    }
    canvas.drawCircle(centerX, centerY, iconSize / 2f, shadowPaint)

    // 5. 아이콘 그리기 (Active 상태에 따른 처리)
    val iconPaint = Paint().apply {
        isAntiAlias = true
        if (!isActive) {
            val cm = ColorMatrix().apply { setSaturation(0f) } // 흑백 처리
            colorFilter = ColorMatrixColorFilter(cm)
            alpha = 150 // 불투명도 조절 (0~255 범위 내)
        }
    }
    canvas.drawBitmap(scaledIcon, centerX - (iconSize / 2f), centerY - (iconSize / 2f), iconPaint)

    // 5. 하단 숫자 뱃지 (기존 조건 유지: 1개면 안 그림)
    if (count > 1) {
        val displayText = when {
            count >= 50 -> "50+"
            count >= 10 -> "10+"
            else -> count.toString()
        }

        val rectPaint = Paint().apply {
            color = if (isActive) Color.parseColor("#6200EE") else Color.DKGRAY
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 32f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        // 뱃지 둥근 사각형 그리기
        val rectWidth = 70f
        val rectHeight = 45f
        val rectTop = centerY + (iconSize / 2f) - 15f
        val rectRect = RectF(centerX - rectWidth/2, rectTop, centerX + rectWidth/2, rectTop + rectHeight)

        canvas.drawRoundRect(rectRect, 20f, 20f, rectPaint)

        // 텍스트 중앙 배치
        val textY = rectRect.centerY() - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(displayText, centerX, textY, textPaint)
    }

    return BitmapDescriptorFactory.fromBitmap(finalBitmap)
}