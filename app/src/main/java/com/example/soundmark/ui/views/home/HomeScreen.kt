package com.example.soundmark.ui.views.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.soundmark.data.model.ClusterMark
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
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
                ClusterDetailContent(selectedCluster!!)
            }
        }
    }
}

@Composable
fun ClusterDetailContent(cluster: ClusterMark) {
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text("이 구역의 사운드 마크 (${cluster.count}개)", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        cluster.pins.forEach { pin ->
            ListItem(
                headlineContent = { Text(pin.track.title) },
                supportingContent = { Text(pin.track.artist) },
                leadingContent = { /* 앨범 커버 이미지 로드 로직 */ }
            )
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