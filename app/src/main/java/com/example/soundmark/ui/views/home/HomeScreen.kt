package com.example.soundmark.ui.views.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val mapPins by viewModel.mapPins.collectAsState()

    // 기본 설정
    val startLocation = LatLng(37.5665, 126.9780)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 15f)
    }

    val uiSettings = MapUiSettings(zoomControlsEnabled = true)
    val properties = MapProperties(isMyLocationEnabled = false)

    // 5. GoogleMap 컴포저블 배치
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        mapPins.forEach { pin ->
            Marker(
                state = rememberMarkerState(position = LatLng(pin.latitude, pin.longitude)),
                title = pin.track.title,
                snippet = pin.track.artist,
                // PRD에 따라 활성화 여부에 투명도 차이를 줄 수 있습니다.
                alpha = if (pin.isActive) 1.0f else 0.5f,
                onClick = {
                    // TODO: 상세 정보 BottomSheet 띄우기 로직
                    false
                }
            )
        }
    }
}