package com.example.soundmark.ui.views.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val startLocation = LatLng(37.5665, 126.9780)
    val musicMarks by viewModel.uiState.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 15f)
    }

    // 초기 데이터 로드 (TODO: 사용자 위치 변화 감지 시 호출)
    LaunchedEffect(Unit) {
        viewModel.fetchNearbyMusic(startLocation)
    }


    // 지도 UI 설정 (줌 버튼 표시 등)
    val uiSettings = MapUiSettings(
        zoomControlsEnabled = true,
        myLocationButtonEnabled = false // 실제 권한 로직 전까지는 꺼둡니다.
    )

    // 지도 속성 설정
    val properties = MapProperties(
        isMyLocationEnabled = false // 실제 권한 로직 전까지는 꺼둡니다.
    )

    // GoogleMap 컴포저블 배치
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        musicMarks.forEach { mark ->
            Marker(
                state = rememberMarkerState(position = mark.location),
                title = mark.title,
                snippet = "${mark.artist} - ${mark.recommendationMessage}",
                alpha = if (mark.isActivated) 1.0f else 0.5f // 거리에 따른 활성화 시각화
            )
        }
    }
}