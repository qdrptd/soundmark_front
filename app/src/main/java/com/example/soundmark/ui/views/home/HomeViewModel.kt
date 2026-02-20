package com.example.soundmark.ui.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.ClusterMark
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.MapPin
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.repository.profile.MarkRepository
import com.example.soundmark.util.MarkerClusteringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val markRepository: MarkRepository
) : ViewModel() {

    private val _rawPins = MutableStateFlow<List<MapPin>>(emptyList())

    // UI는 클러스터링된 마크 리스트를 관찰
    val clusteredMarks: StateFlow<List<ClusterMark>> = _rawPins
        .map { pins -> MarkerClusteringUtil.clusterPins(pins) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 지도에 표시할 마크 리스트 상태
    private val _selectedCluster = MutableStateFlow<ClusterMark?>(null)
    val selectedCluster = _selectedCluster.asStateFlow()

    init {
        loadNearbyPins(GeoLocation.Default)
    }

    fun loadNearbyPins(location: GeoLocation) {
        viewModelScope.launch {
            // ✅ suspend 함수 호출 및 Result 처리
            val result = markRepository.getNearbyMarks(location)
            result.onSuccess { pins ->
                _rawPins.value = pins
            }.onFailure {
                // TODO: 에러 처리 로직 (Toast 등)
            }
        }
    }

    fun onClusterClick(cluster: ClusterMark) {
        _selectedCluster.value = cluster
    }

    fun dismissBottomSheet() {
        _selectedCluster.value = null
    }
}