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

    // UI는 클러스터링된 마크 리스트를 관찰
    // pins와 zoom이 바뀔 때마다 클러스터링을 다시 계산
    val clusteredMarks: StateFlow<List<ClusterMark>> = combine(_rawPins, _currentZoom) { pins, zoom ->
        MarkerClusteringUtil.clusterPins(pins, zoom)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 지도에 표시할 마크 리스트 상태
    private val _selectedCluster = MutableStateFlow<ClusterMark?>(null)
    val selectedCluster = _selectedCluster.asStateFlow()

    init {
        viewModelScope.launch {
            val location = locationService.getCurrentLocation()
            loadNearbyPins(location ?: GeoLocation.Default)
        }
    }

    fun loadNearbyPins(location: GeoLocation) {
        viewModelScope.launch {
            markRepository.getNearbyMarks(location).onSuccess {
                _rawPins.value = it
            }
        }
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