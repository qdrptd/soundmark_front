package com.example.soundmark.ui.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.ClusterMark
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.MapPin
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.repository.profile.MarkRepository
import com.example.soundmark.util.LocationService
import com.example.soundmark.util.MarkerClusteringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val markRepository: MarkRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _rawPins = MutableStateFlow<List<MapPin>>(emptyList())
    private val _currentZoom = MutableStateFlow(15f)
    private var currentUserLocation: GeoLocation = GeoLocation.Default
    private var lastRequestedLocation: GeoLocation? = null

    // UI는 클러스터링된 마크 리스트를 관찰
    // pins와 zoom이 바뀔 때마다 클러스터링을 다시 계산
    val clusteredMarks: StateFlow<List<ClusterMark>> = combine(_rawPins, _currentZoom) { pins, zoom ->
        MarkerClusteringUtil.clusterPins(pins, zoom)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 지도에 표시할 마크 리스트 상태
    private val _selectedCluster = MutableStateFlow<ClusterMark?>(null)
    val selectedCluster = _selectedCluster.asStateFlow()

    init {
        refreshCurrentGpsAndLoad()
    }

    // 1. 초기화 혹은 내 위치 버튼 클릭 시 호출
    fun refreshCurrentGpsAndLoad() {
        viewModelScope.launch {
            val location = locationService.getCurrentLocation() ?: GeoLocation.Default
            currentUserLocation = location // 실제 GPS 위치 갱신
            loadNearbyPins(mapCenter = location) // 초기엔 지도 중심도 내 위치로
        }
    }

    fun onCameraMoved(newLocation: GeoLocation) {
        val lastLocation = lastRequestedLocation
        if (lastLocation == null) {
            loadNearbyPins(newLocation)
            return
        }

        // 거리를 계산하여 일정 거리(예: 100m) 이상 이동했을 때만 호출
        val distance = calculateDistance(
            lastLocation.latitude, lastLocation.longitude,
            newLocation.latitude, newLocation.longitude
        )

        if (distance > 100) { // 100미터 이상 이동 시 갱신
            loadNearbyPins(newLocation)
        }
    }

    // 핵심: 지도 중심(location)과 내 실제 위치(currentUserLocation)를 모두 전송
    private fun loadNearbyPins(mapCenter: GeoLocation) {
        lastRequestedLocation = mapCenter
        viewModelScope.launch {
            markRepository.getNearbyMarks(
                geoLocation = mapCenter,
                userLocation = currentUserLocation
            ).onSuccess {
                _rawPins.value = it
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    fun onClusterClick(cluster: ClusterMark) {
        _selectedCluster.value = cluster
    }

    fun dismissBottomSheet() {
        _selectedCluster.value = null
    }

    fun onZoomChanged(newZoom: Float) {
        _currentZoom.value = newZoom
    }

}