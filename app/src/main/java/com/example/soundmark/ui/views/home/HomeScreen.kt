package com.example.soundmark.ui.views.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen() {
    // 1. 지도의 초기 위치 설정 (예: 서울 시청)
    val startLocation = LatLng(37.5665, 126.9780)

    // 2. 카메라 상태 기억 (줌 레벨 15)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 15f)
    }

    // 3. 지도 UI 설정 (줌 버튼 표시 등)
    val uiSettings = MapUiSettings(
        zoomControlsEnabled = true,
        myLocationButtonEnabled = false // 실제 권한 로직 전까지는 꺼둡니다.
    )

    // 4. 지도 속성 설정
    val properties = MapProperties(
        isMyLocationEnabled = false // 실제 권한 로직 전까지는 꺼둡니다.
    )

    // 5. GoogleMap 컴포저블 배치
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        // 여기에 나중에 Marker(핀)를 추가할 예정입니다!
    }
}