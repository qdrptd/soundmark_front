package com.example.soundmark.ui.views.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import com.example.soundmark.data.model.SoundMark
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
                        icon = createClusterIcon(cluster.count, cluster.isActive),
                        onClick = {
                            viewModel.onClusterClick(cluster)
                            true
                        }
                    )
                }
            }
        }

        // ClusterMark를 눌렀을 때 나오는 BottomoSheet
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
                imageVector = androidx.compose.material.icons.Icons.Default.Lock,
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

// 숫자가 적힌 동그란 비트맵 아이콘 생성 함수
fun createClusterIcon(count: Int, isActive: Boolean): BitmapDescriptor {
    val size = 100
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint().apply {
        color = if (isActive) Color.parseColor("#6200EE") else Color.parseColor("#9E9E9E")
        isAntiAlias = true
    }

    // 배경 동그라미
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    // 숫자 텍스트
    paint.apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }
    val xPos = canvas.width / 2f
    val yPos = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2f)
    canvas.drawText(count.toString(), xPos, yPos, paint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}